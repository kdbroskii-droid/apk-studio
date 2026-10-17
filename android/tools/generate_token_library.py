#!/usr/bin/env python3
"""Build APK Studio's large, stable five-digit token dictionary.

The first 200 mappings are kept in MainActivity.kt. This script appends a
curated gaming/programming vocabulary, common typos, and CMUdict words using
stable sequential codes. Codes 99000-99999 are deliberately reserved for
runtime additions. Generated output is a build artifact, not committed data.
"""
from __future__ import annotations

import re
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ACTIVITY = ROOT / "app/src/main/java/dev/apkstudio/mobile/MainActivity.kt"
OUT = ROOT / "app/src/main/assets/token-library-extra.tsv"
CMUDICT_URL = "https://raw.githubusercontent.com/cmusphinx/cmudict/master/cmudict.dict"
FIRST_EXTRA = 201
LAST_STATIC = 98999

GAMING_AND_DEV = """
aimbot aimbotting aimbotted aim assist aim-assist wallhack wall hack esp triggerbot trigger bot
silent aim recoil no recoil rapidfire rapid fire autofarm auto farm autofarm bot exploit exploiter
cheat cheating anti-cheat anticheat ban evasion bypass lag switch speedhack speed hack flyhack fly hack
noclip no clip godmode god mode hitbox hit box hitbox extender reach extender reachhack reach hack
autoclicker auto clicker macro macros script executor inject injection injector decompiler decompile
remote spy remote event remote function server authority server-authoritative client-side server-side
datastore data store leaderstats leaderboard obby tycoon simulator battlegrounds roleplay pvp pve fps
tps rpg mmorpg moba battle royale sandbox survival crafting building parkour speedrun speedrunning
spawn respawn despawn checkpoint quest mission objective npc bot bots ai pathfinding navmesh hit detection
raycast raycasting collision collider physics ragdoll knockback stun cooldown debounce debounce timer
inventory hotbar toolbar backpack equipment equip unequip consumable crafting recipe loot drop rarity
common uncommon rare epic legendary mythic divine exotic chromatic rainbow shiny golden gold silver
mutation upgrade prestige rebirth ascension level xp experience currency coins cash gems tokens credits
premium gamepass developer product marketplace purchase receipt robux roblox studio luau lua
unreal unreal engine unity godot blender mesh texture material shader uv unwrap rig rigging animation
keyframe tween easing particle vfx sfx sound effect soundtrack ambience lighting shadow reflection
render renderer viewport camera fov field of view resolution fps drop stutter lag spike ping latency
packet replication desync exploit patch hotfix update rollback regression bugfix bug report crash freeze
memory leak race condition deadlock thread coroutine async await promise callback event listener
api endpoint route request response payload json yaml xml csv tsv websocket socket tcp udp http https
authentication authorization oauth token bearer secret credential api key rate limit quota throttling
fallback retry backoff timeout exception stacktrace stack trace log logger debugging debugger profiling
compile compiler build gradle kotlin java javascript typescript python rust csharp c sharp cpp c plus plus
html css react native android ios apk aab manifest permission activity fragment service intent uri
accessibility responsive adaptive layout constraint padding margin spacing alignment anchor viewport
button textbox text box textfield text field dropdown checkbox radio switch slider progressbar progress bar
toast snackbar dialog modal popup tooltip tab navbar navigation drawer sidebar panel card grid list
scrollview scroll view recycler view adapter viewmodel repository database sqlite room shared preferences
filesystem file system directory folder path archive zip unzip extract import export save load autosave
backup restore sync synchronize cloud offline online network connection disconnect reconnect download upload
encryption hash checksum signature certificate keystore signing obfuscation minify shrink proguard r8
unit test integration test ui test smoke test test case assertion mock stub fixture coverage ci cd pipeline
github git branch commit merge pull request workflow action artifact release tag issue pull-request
robloxgame robloxdev robloxdeveloper gamejam gamedev indiegame devlog playtest testserver private server
obbyobbyobby afk gg ez rekt cracked crackedgamer sweat tryhard noob newbie pro proplayer godtier op nerf buff
grind grinding farming farmingbot alt account smurf spawncamp spawn camp camper camping grief griefer griefing
teamkill team kill friendlyfire friendly fire headshot noscope no scope quickscope quick scope flick flickshot
aimbotter wallhacker exploiter alt altacc alt account script kiddie mod menu modmenu modded hacked client
fpsunlocker fps unlocker shaderpack texturepack resourcepack datapack modpack modloader plugin addon add-on
laggy buggy glitchy glitch exploit dupe duplication duplication glitch item dupe money dupe infinite money
infinite coins unlimited money unlimited health one shot oneshot one-shot instant kill instakill autoaim auto aim
antiaim anti aim prediction lead target tracking target lock aimlock aim lock recoil control recoilcontrol
teleport teleportation teleport hack teleporthack instant teleport speed boost jump boost super jump
superjump double jump triple jump infinite jump fly flying hover glide dash blink sprint run speed walk
godmodehack invincibility invisible invisibility invis hack vanish stealth spectate spectator freecam free cam
camera hack camera noclip through walls see through walls wall vision player tracker player esp box esp skeleton esp
tracers tracer lines name esp nametags nametag overlay overlay menu gui menu keybind hotkey toggle keybinds
aimbotting aimbottingg aimobt aimob aimtbot aimbto aimbo aim bot aim-bot aimbottt aimbtoer
teh thee thier their recieve receive seperate separate definately definitely accomodate accommodate occured occurred
untill until wich which becuase because becasue because teh the adress address addres addresss recieve receive
wierd weird freind friend frend friendd thier their thier their seperate separate seprate separate
definately definitely definatly definitely probly probably prolly probably tommorow tomorrow tommorrow tomorrow
alot a lot alot a lot alotof a lot of could of could've would of would've should of should've your you're
youre you're its it's dont don't doesnt doesn't cant can't wont won't isnt isn't didnt didn't wasnt wasn't
im i'm ive i've id i'd ill i'll lets let's thats that's whats what's didnt didn't wasnt wasn't
recieve recived received recieveing receiving acheive achieve acheived achieved beleive believe beleived believed
begining beginning comming coming runing running stoped stopped seperate separate occassion occasion
untill until finaly finally definately definitely independant independent goverment government enviroment environment
wierd weird thier their freind friend adress address tommorow tomorrow neccessary necessary necesary necessary
usefull useful basicly basically realy really literaly literally actualy actually usualy usually
acount account acces access adress address aggresive aggressive apparant apparent arguement argument
beleive believe calender calendar cemetry cemetery concious conscious curiousity curiosity decieve deceive
embarass embarrass existance existence fourty forty grammer grammar harrass harass immediatly immediately
independant independent knowlege knowledge liason liaison millenium millennium occassion occasion
occured occurred persue pursue priviledge privilege recomend recommend refered referred relevent relevant
repetion repetition resistence resistance succesful successful suprise surprise temprature temperature
therefor therefore threshhold threshold truely truly wierd weird writting writing
""".split()

# Preserve useful multiword concepts in the vocabulary; codes map to meanings,
# so spaces are intentional and the encoder can match phrase-level concepts.
PHRASES = """
aimbot=aimbot cheat
wallhack=wallhack cheat
triggerbot=triggerbot cheat
silent_aim=silent aim cheat
aim_assist=aim assistance
no_recoil=no recoil
auto_farm=automatic farming
speed_hack=speed hack
fly_hack=flying cheat
god_mode=invincibility mode
noclip=collision bypass
anti_cheat=anti-cheat system
remote_event=Roblox RemoteEvent
remote_function=Roblox RemoteFunction
server_authoritative=server-authoritative design
pathfinding=AI pathfinding
hit_detection=hit detection
data_store=Roblox DataStore
leaderstats=Roblox leaderstats
game_pass=Roblox game pass
developer_product=Roblox developer product
rate_limit=rate limiting
api_key=API key
stack_trace=stack trace
race_condition=race condition
memory_leak=memory leak
""".splitlines()

def get_base_terms() -> set[str]:
    source = ACTIVITY.read_text(encoding="utf-8")
    found = re.findall(r'"\d{5}=([^"]+)"', source)
    return {term.strip().lower() for term in found}

def cmudict_words() -> list[str]:
    request = urllib.request.Request(CMUDICT_URL, headers={"User-Agent": "APK-Studio-token-library-builder"})
    with urllib.request.urlopen(request, timeout=60) as response:
        text = response.read().decode("utf-8", errors="ignore")
    words = []
    for line in text.splitlines():
        if not line or line.startswith(";;;") or " " not in line:
            continue
        word = line.split()[0].lower()
        word = re.sub(r"\(\d+\)$", "", word)
        if re.fullmatch(r"[a-z][a-z'-]{0,30}", word):
            words.append(word)
    return words

def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    base = get_base_terms()
    terms: list[str] = []
    seen = set(base)

    def add(term: str) -> None:
        term = term.strip().lower()
        if not term or term in seen or len(term) > 60:
            return
        if not re.fullmatch(r"[a-z][a-z0-9_' -]*", term):
            return
        seen.add(term)
        terms.append(term)

    for phrase in PHRASES:
        if "=" in phrase:
            key, meaning = phrase.split("=", 1)
            add(key.replace("_", " "))
            add(meaning)
        else:
            add(phrase.replace("_", " "))
    for term in GAMING_AND_DEV:
        add(term)
    # Add likely single-edit misspellings for common prompt/game words.
    common = "script generate create change update build game player character health damage speed jump teleport inventory potion garden camera button function variable request response project library dictionary token number string boolean server client roblox android kotlin python javascript unreal unity".split()
    for word in common:
        for i in range(len(word)):
            add(word[:i] + word[i+1:])              # deletion
        for i in range(len(word)-1):
            if word[i] != word[i+1]:
                add(word[:i] + word[i+1] + word[i] + word[i+2:])  # transposition
        for i, char in enumerate(word):
            for replacement in "abcdefghijklmnopqrstuvwxyz":
                if replacement != char:
                    add(word[:i] + replacement + word[i+1:])     # substitution
    for word in cmudict_words():
        add(word)

    max_count = LAST_STATIC - FIRST_EXTRA + 1
    terms = terms[:max_count]
    with OUT.open("w", encoding="utf-8", newline="\n") as output:
        for index, term in enumerate(terms, start=FIRST_EXTRA):
            output.write(f"{index:05d}={term}\n")
    print(f"Generated {len(terms)} additional token mappings ({FIRST_EXTRA:05d}-{FIRST_EXTRA + len(terms) - 1:05d}); 99000-99999 reserved.")

if __name__ == "__main__":
    main()
