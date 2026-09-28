# Schema Unknowns

## Data Types

- `OBJECTIVES.required` (typically 1.0; suspected flag)
- `RULES.room_info_indicate_major`, `RULES.enemy_death_memory` (typically 0.0; suspected flag)
- `RULES.save_style` (typically 0.0; suspected integer 0..1)
- `SAMUS.weapons.bomb_scale_damage_from_beams` (typically 1.0; suspected flag)
- `SAMUS.weapons.shinespark_damage_from_suits` (typically 0.0; not sure if flag or integer)
- `SAMUS.weapons.charge_combo_ammo` (typically 1.0; suspected integer)
- `SAMUS.movement.spin_jump_timing` (typically 0.0; suspected integer)

- `ROOMS[*].PARALLAX.scroll_auto` (typically 0.0; suspected flag)

- bit labels for pathing item bitmasks such as `STATS.pathing_items`

## Unknown Purpose
- room `STATS.region.list` and `STATS.region.mask` in the region struct

- for that matter, metadata.stats, how much of that is actually needed in a world?
- `ship_hints` and `hazard_runs` look flag-y, but are "real" in the json. Are those integers or booleans?


## Unknown Data
- `ROOMS[*].SCREENS[*].OBJECTS[*].type` values 2 and 9 are unknown objects. It would be helpful to have "closed" property sets for these.
- obviously we're all highly anticipating clarity for `LIQUIDS`, `DECOR`, and `TUBES`, but as these are empty arrays in practice, I'm specifying the empty array constant for now.





## Prioritized

- "stats" block is required, being one of the more drastic and persistent game crashes if the key is removed.
  -
- Is there a way to get "description" to appear instantly instead of the normal text crawl? "modified" turns off the built-in stats block, but I'd like to give users some idea of what a map is like when they load it in.
