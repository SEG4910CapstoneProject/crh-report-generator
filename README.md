# report-generator

The report generator is a task that generates reports based on the contents of the database

## Environment Variables
| Environment Variable    | Description                                                                            |
|-------------------------|----------------------------------------------------------------------------------------|
| POSTGRES_HOST           | The hostname of the postgres database                                                  |
| POSTGRES_PORT           | The port of the postgres database                                                      |
| POSTGRES_DB_NAME        | The name of the db for the postgres database                                           |
| POSTGRES_USERNAME       | Postgres username for access. Should contain write permissions                         |
| POSTGRES_PASSWORD       | Postgres password for authentication                                                   |
| REPORT_GEN_REPORT_TYPE  | The type of report to generate. Only daily supported currently. **Default: daily**     |
| DB_MAX_RETRIES          | Max number of retries to conduct when db requests fail. **Default: 3**                 |
| DB_RETRY_BACKOFF_MILLIS | period in milliseconds between retries. **Default: 3000**                              |
| MAX_SUGGESTED_ARTICLES  | The maximum number of articles that can be suggested by the generator. **Default: 15** | 
