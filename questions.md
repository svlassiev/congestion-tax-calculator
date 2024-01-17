# Questions

## Use cases
To clarify API we need to understand use cases. For now, we assume it's internal service which is only reachable by authorized services. So, no extra authentication and authorization is done.

1. Could this service handle its own data or should it rely on input from the caller?
2. How to get public holidays for Gothenburg?
3. In what format do we get date and time when the vehicle crossed the toll? Let's assume it's linux time in UTC. This assumption requires translation to Gothenburg time zone.
4. Should we return full fee or have a split for a fee per day?
5. How does 60 minutes interval work? I assume this: when the first toll pass happens then for 60 minutes all the passes counts for the same window and then the most expensive pass is calculated as a tax. New window starts only after first window is closed.