# exaqua:handsieve
This recipies affect drops from hand sieve.
## Example
```
{
  "type": "exaqua:handsieve",
  "success": 20,
  "fluid": "exaqua:mud_fluid",
  "outputs": [
    {
      "item": {"item": "minecraft:wheat_seeds"},
      "weight": 7
    },
    {
      "item": {"item": "minecraft:beetroot_seeds"},
      "weight": 4
    },
    {
      "item": {"item": "minecraft:cobblestone", "count":2},
      "weight": 3
    },
    {
      "item": { "item": "minecraft:dirt"},
      "weight": 2
    },
    {
      "item": { "item": "minecraft:carrot"},
      "weight": 1
    }
  ]
}
```
## parametrs 
*success*: Chance (in percents) that player gets an item. If roll succeeds player gets item 1 item from *outputs*  (max 100)
*fluid*: What fluid needs to be in the sieve for this recipie.
*outputs*: Array of items that player can get. Contains a JSON object with 2 properties:
    *item*:Json object of minecraft Itemstack {"item":"item id"}. Can contain *count* if you want to set amount of items {"item": "minecraft:cobblestone", "count":2}. Cannot use tag.
    *weight*: Weight that this item will be selected. Must be bigger than 0. Higher => bigger chance compared to other items.

# exaqua:sieve
This recipies affect drops from all auto sieves.
## Example
```
{
  "type": "exaqua:sieve",
  "input":
    {
      "fluid": "minecraft:water",
      "amount": 100
    },
  "outputs": [
    {
      "item": {"item": "minecraft:sand"},
      "weight": 6
    },
    {
      "item": { "item": "minecraft:gravel"},
      "weight": 7
    }

  ],
  "time": 25,
  "rf": 35,
  "tier":"gold"
}
```
## parametrs
*input*: Json object of fluid needed for this recipie.
  *fluid*: Id (resource location) of the fluid
  *amount*: Amount (in miliBuckets) that will be used up for one output.
*outputs*: Array of items that player can get. Contains a JSON object with 2 properties:
    *item*: Json object of minecraft Itemstack {"item":"item id"}. Can contain *count* if you want to set amount of items {"item": "minecraft:cobblestone", "count":2}. Cannot use tag.
    *weight*: Weight that this item will be selected. Must be bigger than 0. Higher => bigger chance compared to other items.
*time*: Time it takes to do one recipie (in ticks).
*rf*: Rf used every tick.
*tier*: What tier of sieve is needed for this recipie. Options are: iron, gold, frogium, diamond. 

# exaqua:squeezer
Recipies for squeezer and auto squeezer. This is only recipie that uses Ingridients insted of Itemstacks, meaning you cannot use *count*, but can use *tag*.
## Example
{
  "type": "exaqua:squeezer",
  "input":
    {
      "item": "minecraft:apple"
    },
  "output": {
    "fluid": "minecraft:water",
    "amount": 70
  }
}
## parametrs
*input*: Json object of minecraft Ingridient {"item":"item id"}. $${\color{This is only recipie that uses Ingridients insted of Itemstacks, meaning you cannot use *count*, but can use *tag* {"tag": "minecraft:leaves"}.}Red}$$
*output*: Json object of fluid player gets by this recipie.
    *fluid*: Id (resource location) of the fluid
    *amount*: Amount (in miliBuckets) that will be produced as a output.

# exaqua:cauldron
Recipies for crafting cauldron.
## Examples
{
  "type": "exaqua:cauldron",
  
  "input_fluid": "minecraft:water",
  "input_item": {"item": "minecraft:acacia_leaves" ,"count": 6},
  "output_fluid":"exaqua:mud_fluid"
  "temperature": "neutral",
}
{
  "type": "exaqua:cauldron",
  "input_fluid": "minecraft:water",
  "input_item": {"item": "minecraft:podzol"},
  "output_fluid": "exaqua:mud_fluid",
  "Output_item": {"item":"minecraft:dirt"},
  "temperature": "neutral"
}
{
  "type": "exaqua:cauldron",
  "input_fluid": "minecraft:water",
  "Output_item": {"item": "minecraft:ice"},
  "temperature": "cold",
  "amount_input": 1000
}
{
  "type": "exaqua:cauldron",
  "input_item": {"item": "minecraft:cobblestone"},
  "output_fluid": "minecraft:lava",
  "temperature": "hot",
  "amount_Output": 50
}
## parametrs
*input_fluid*: Id (resource location) of the fluid needed as a input. If set to empty or not used, recipie will need the cauldron to be empty.
*amount_input*: Amount of *input_fluid*  (in miliBuckets) that will be needed as a input.
*input_item*: Json object of minecraft Itemstack {"item":"item id"}. Can contain *count* if you want to set amount of items {"item": "minecraft:cobblestone", "count":2}. Cannot use tag.


*output_fluid*: Id (resource location) of the fluid produced as a input.
*amount_Output*: Amount of *output_fluid*  (in miliBuckets) that will be produced as output. $${\color{If recipie also requeres *input_fluid* it will transform all fluid in cauldron into *output_fluid* and ignore this parametr.}Red}$$
*Output_item*: Json object of minecraft Itemstack {"item":"item id"}. Can contain *count* if you want to set amount of items {"item": "minecraft:cobblestone", "count":2}. Cannot use tag.

*temperature*: Temperature needed for recipie. Set by changing block under the cauldron. Options are: cold, neutral, hot.

