# TRADEOFFS

## Language Choice

I picked Java based on a mix of safety and ecosystem support.

Parsing Twitter’s archive involves nested JSON. Java’s static typing
caught mismatches at compile time, so I didn’t have to wait until
runtime to discover that a field name or structure didn’t line
up.

Ecosystem: Libraries like Gson, OpenCSV, and Google’s GenAI SDK all have
mature Java support. That meant fewer surprises and reliable documentation.

Error handling: Checked exceptions forced me to be explicit about I/O and
API failures. This made retry logic, file recovery, and fallback behavior
more predictable.

Python would have been faster to write, but Java’s type system prevented
subtle bugs and made the whole pipeline feel sturdier. With newer features
like records and text blocks, verbosity wasn’t much of a problem.

## Architecture

I used a small component-based design so each responsibility stayed
isolated:

- csv package: writes deletion results and maintains a checkpoint file
- ai_client package: handles GenAI calls, rate limits, and retries
- parser package: unwraps Twitter’s JS-style JSON and maps it into Java
  records
- orchestrator package: coordinates workflow - load state, parse tweets,
  evaluate, save results
- Interface-based: Orchestrator depends on `TweetEvaluator` interface,
  not a concrete AI client, so, swapping providers only requires one new
  implementation.

This setup keeps the code approachable, easy to test, and simple to adjust
in the future

## Error Handling

The app is built to keep moving even when things fail.
Retry logic: exponential backoff (1s, 2s, 4s delays)
handles transient failures like network or api issues.

Checkpoint recovery: after each tweet is evaluated, its ID is immediately
persisted so the program never reprocesses old work.

Graceful fallback: tweets that still fail after all retries get tagged as
`API_ERROR` and the system continues with the next one.

The idea is to avoid stopping the whole job because of a single problematic
tweet.

## Concurrency Strategy

I processed tweets one at a time, intentionally.
Why sequential?

- It keeps the logic simple
- The 4-second gaps naturally stay under Gemini’s rate limits
- Retries are isolated and predictable
- Checkpointing is straightforward - check point after each tweet, write 
  and move on

Batching 10–15 tweets concurrently would reduce total runtime, but it would
also introduce thread coordination, synchronized I/O, and more complicated
error handling. For a one-off data-cleaning task, that extra complexity
didn’t feel worth it

## Performance vs Safety

- Sequential processing is slower but safer
- Per-tweet checkpoints reduce the risk of losing progress
- Retry attempts add a few extra seconds but prevent transient failures
  from breaking the pipeline
- Conservative rate limiting avoids hitting API blocks

Design principle: this application favours reliability over speed. When a
process runs for hours unattended, preventing crashes and data loss
matters more than shaving 30 minutes off runtime.