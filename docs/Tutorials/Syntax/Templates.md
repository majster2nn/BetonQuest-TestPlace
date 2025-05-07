---
icon: material/folder-open
tags:
  - Templates
---

# :material-folder-open: Templates

This tutorial will teach you the basics of templates usage as well as to when you probably want to use them

<div class="grid" markdown>
!!! danger "Requirements"
    Doing this tutorial helps but is not strictly required:
    
    * [Basic Tutorial](../Getting-Started/About.md)
    * [Variables](../../Documentation/Scripting/Building-Blocks/Variables-List/#constant-variable

!!! example "Related Docs"
    * [Package Structure Reference](../../Documentation/Scripting/Packages-&-Templates.md#structure)
    * [Templates](../../Documentation/Scripting/Packages-&-Templates.md#templates)
</div>

## 1. What is a Template?

A template is a form of package that's not loaded by BQ as normal quest package (with which you should already be familiar with) 
and won't work as a standalone package, instead templates are used as a base for other quest packages. Quest packages that use templates inherit theirs
events, objectives and other declared values. 

It's similar to normal quest package as it needs `package.yml` to be present inside, of a folder, and the internal file structure is about the same.

So basically it's just a not loaded quest package that's used as a base for normal quest packages that inherit templates events, objectives, conditions, etc. 

## 2. Creating and using a Template.

Let's take a look at a basic template package, shall we?

!!! example annotate "Basic Template Structure"
    - :material-folder-open: QuestTemplates
        - :material-folder-open: generalQuest
            - :material-file-star: package.yml

!!! example "Package Contents"
    ```YAML title="package.yml"
    events:
      startQuest: "tag add questStarted"
      finishQuest: "tag add questFinished"
              
    conditions:
      questStarted: "tag questStarted"
      questFinished: "tag questFinished"
    ```

As you can see it's nothing special, just basic events and conditions to check if quest is started and finished.  

!!! info "Package placement"
    Notice that the package is under QuestTemplates not QuestPackages, that is important for BQ to register it as a 
    Template and not a normal Quest Package. 

Now let's dive into how you can use it inside, of a Quest Package.

!!! example annotate "Basic Template and Quest Package Structure"
    - :material-folder-open: QuestPackages
        - :material-folder-open: introduction
            - :material-file-star: package.yml
        - :material-folder-open: tutorial
            - :material-file-star: package.yml
    - :material-folder-open: QuestTemplates
        - :material-folder-open: generalQuest
            - :material-file-star: package.yml

!!! example "File contents before using templates"
    === "introduction"
        ```YAML title="package.yml"
        events:
          teleportToSpawn: "teleport 0;64;0;world" 
          startQuest: "tag add questStarted"
          finishQuest: "tag add questFinished"
                      
        conditions:
          questStarted: "tag questStarted"
          questFinished: "tag questFinished"
        ```
    === "tutorial"
        ```YAML title="package.yml" 
        objectives:
          breakLogs: "block .*_LOG -4 events: brokeLogs"
          craftPickaxe: "craft woodenPickaxe 1 events: finishQuest"
        
        items:
          woodenPickaxe: "simple WOODEN_PICKAXE"
          
        events:
          brokeLogs: "notify You broke enough logs! \nIt's time to craft a pickaxe!"
          craftedPickaxe: "notify You crafted a pickaxe!"
          startQuest: "tag add questStarted"
          finishQuest: "tag add questFinished"
        
        conditions:
          questStarted: "tag questStarted"
          questFinished: "tag questFinished"
        ```
        
You can recognize with the knowledge that you already should have, that these are some really basic quest packages. 
However instead of writing events for start and end of the quest in those files separately we've done it in the 
template before. So instead of writing duplicated code we can now do this:

!!! example "File contents after using templates"
    === "introduction"
        ```YAML title="package.yml"
        package:
          {==templates==}:
            - {==generalQuest==}
            
        events:
          teleportToSpawn: "teleport 0;64;0;world" 
        ```
    === "tutorial"
        ```YAML title="package.yml"
        package:
          {==templates==}:
            - {==generalQuest==}
            
        objectives:
          breakLogs: "block .*_LOG -4 events: brokeLogs"
          craftPickaxe: "craft woodenPickaxe 1 events: finishQuest"
        
        items:
          woodenPickaxe: "simple WOODEN_PICKAXE"
          
        events:
          brokeLogs: "notify You broke enough logs! \nIt's time to craft a pickaxe!"
          craftedPickaxe: "notify You crafted a pickaxe!"
        ```

Now both packages have access to events and conditions we wrote earlier in the template so `introduction` package 
can use startQuest and finishQuest events as well as `tutorial` package can. 

You can also use multiple templates in one package. Let's create a simple package that keeps track on if we talked 
to an NPC or not.

!!! example annotate "Multi Template Structure"
    - :material-folder-open: QuestTemplates
        - :material-folder-open: generalQuest
            - :material-file-star: package.yml
        - :material-folder-open: npcConversation
            - :material-file-star: package.yml

!!! example "Package npcConversation Contents"
    ```YAML title="package.yml"
    events:
      talkedWithTheNPC: "tag add talkedWithTheNPC"
              
    conditions:
      hasTalkedWithTheNPC: "tag talkedWithTheNPC"
    ```

And let's say that the `tutorial` Quest Package should have an NPC that needs this condition then we just simply add 
this template to the templates section as well.

!!! example "File contents after using templates"
    === "tutorial"
        ```YAML title="package.yml"
        package:
          templates:
            - generalQuest
            - {==npcConversation==}
            
        objectives:
          breakLogs: "block .*_LOG -4 events: brokeLogs,startCraftPickaxe"
          craftPickaxe: "craft woodenPickaxe 1 events: finishQuest"
        
        items:
          woodenPickaxe: "simple WOODEN_PICKAXE"
          
        events:
          startBreakLogs: "objective add breakLogs"
          startCraftPickaxe: "objective add craftPickaxe"
          brokeLogs: "notify You broke enough logs! \nIt's time to craft a pickaxe!"
          craftedPickaxe: "notify You crafted a pickaxe!"
        ```

And now the `tutorial` Quest Package also has access to all the things inside `npcConversation` template but 
`introduction` doesn't. If you want to tinker with what we have up to this point then you can download this tutorial 
package.

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Syntax/templates /packageStructure/SingleFile
    ```
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestTemplates /Syntax/templates
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/SingleFile_"

## 3. Overwriting Template's content

Stuff that we have inside, of out templates can be overwritten in the packages that use the template for this 
specific package only. For example lets say that your template uses a constant variable for quest item 
material, and you want to change it for different packages while still keeping universal objective with events, 
overwriting constants will work great here, let's see how it could look like.

!!! example annotate "Quest Item Template"
    - :material-folder-open: QuestTemplates
        - :material-folder-open: craftItemsGenericQuest
            - :material-file-star: package.yml

!!! example "craftItemsGenericQuest"
    ```YAML title="package.yml"
    events:
      startCraftObjective: "objective add craftObjective"
      startQuest: "tag add questStarted"
      finishQuest: "tag add questFinished"      

    objectives:
      craftObjective: "craft questItem 1 events: finishQuest"
      
    items:
      questItem: "simple %constant.questItemMaterial%"
    
    constants:
      questItemMaterial: "TORCH"
      
    conditions:
      questStarted: "tag questStarted"
      questFinished: "tag questFinished"
    ```
