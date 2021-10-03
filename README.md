# ConwaysGameOfLife
My Java version of the famous "Conway's Game of Life"


## Controls
- `R` randomises the board.
- `C` clears the board.
- `P` pauses/unpauses the game (you can only unpause on the latest generation).

- `1` sets the randomness threshold so that there is a 30% chance of a cell being alive when you press `R`.
- `2` sets the randomness threshold so that there is a 50% chance of a cell being alive when you press `R`. This is the default threshold.
- `3` sets the randomness threshold so that there is a 80% chance of a cell being alive when you press `R`.

- The left arrow steps back a generation, pausing the game
- The right arrow steps forward a generation
- The up arrow steps jumps to the first generation, pausing the game
- The down arrow jumps to the latest generation, unpausing the game

- `+` / `=` increases the ticks per second by 5 (capping at 60)
- `-` / `_` decreases the ticks per second by 5 (ending at 5)

- Left clicking on the board (incl. dragging) draws on the board, temporarily pausing the game if not already paused
- Right clicking on the board (incl. dragging) erases on the board, temporarily pausing the game if not already paused
