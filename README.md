# Tweet Audit Tool

Analyze your Twitter/X archive using AI to identify tweets for deletion.

## Features

- Parses Twitter archive JSON files
- Evaluates tweets using Google's Gemini AI (also, easy integration of
  other models)
- Checkpoint-based resumption (crash-safe)
- Configurable evaluation criteria
- Rate-limited API calls
- Generates CSV of tweets to delete

## Prerequisites

- Java 21+
- Maven
- Google Gemini API key
- Twitter/X archive download

## Setup

- Get your Twitter archive: Settings ----> Download archive
- Get Gemini API key
  : [Google AI Studio](https://aistudio.google.com/apikey)
  - Set environment variable:

For Linux/Mac:

```bash
   export GEMINI_API_KEY=your_key_here
   echo 'export GEMINI_API_KEY=your_key_here' >> ~/.bashrc
```

For Windows (Powershell):

```powershell
    $env:GEMINI_API_KEY="your_key_here"
    [System.Environment]::SetEnvironmentVariable('GEMINI_API_KEY', 'your_key_here', 'User')
```

For Windows (Command Prompt):

```cmd
  set GEMINI_API_KEY=your_key_here
  setx GEMINI_API_KEY "your_key_here"
```

- Verify it's set:

```bash
  echo $GEMINI_API_KEY # linux/mac/git bash
  echo %GEMINI_API_KEY% # windows cmd
```

- Restart your terminal after setting variables.

```bash
  exit # linux/mac/git bash
```

## Usage

```bash
  mvn clean package
  java -jar target/tweet-audit-1.0-SNAPSHOT.jar <archive_path> <output_csv> <config_path>
```

Example:

```bash
  java -jar target/tweet-audit-1.0-SNAPSHOT.jar data/tweets.js results.csv criteria.json
```

## Configuration

Create `criteria.json` (in the root folder):

```json
{
  "forbiddenWords": [
    "crypto",
    "NFT"
  ],
  "professionalCheck": true,
  "tone": "respectful",
  "excludePolitics": false
}
```

## Output

- results.csv: Tweets flagged for deletion with reasons
- results_checkpoint.txt: Progress tracker for resume capability

## How It Works

1. Loads previously processed tweet IDs (resumes if interrupted)
2. Parses Twitter archive
3. Sends unprocessed tweets to Gemini for evaluation
4. Saves flagged tweets to CSV
5. Updates checkpoint after each tweet

Rate-limited to 15 requests/minute. Retries failed requests 3 times with
exponential backoff.

## Project Structure

```
src/main/java/
    AI_Client/      # Gemini API integration
    csv/            # State management
    parser/         # Twitter archive parsing
    orchestrator/   # Main workflow
    Main.java       # Entry point
    criteria.json   # Your tweet evaluation criteria
```

See [TRADEOFFS.md](TRADEOFFS.md) for architecture decisions.
