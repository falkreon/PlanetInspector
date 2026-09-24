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