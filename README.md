# lucid-lite

## What is it?
lucid-lite is the lite version of lucid that caters more towards performance and compatibility. This version of the mod does not prevent vanilla sleep, instead it calculates how much time was skipped and ticks block entities at a faster rate to make up for the lost time. The mod consists of 2 injects into the `tick` method of the `ServerWorld` class, no redirects.

## Performance
How much this mod impacts performance is up to you, the user. By default the mod will consume no more than 10ms of time each tick, this can be configured by placing a file called `lucid-lite.config` in your minecraft instance config folder with the content `tickBudget: x` where x is how many ms per tick the mod can spend ticking block entities. With this value set to 0, the mod will use as much time as it wants which can result in a large lag spike that should go pretty much unnoticed in singleplayer (as you are sleeping) but may cause problems for servers.

## Known Issues
None that I know of.

## Compatibility
Hopefully compatible with everything, if it is not, it is likely not this mods fault.
