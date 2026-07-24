Ensure migration scripts are developed to support several executions over the same database without any error.
This feature will make sure this migration execution would be compatible with other needed flyway migrations without broking the chain.

IMPORTANT: Due to the strong dependency between this module and data-index-storage-jpa please be sure that any new
Flyway migration added here uses a consistent version that doesn't collide with data-index-storage-jpa