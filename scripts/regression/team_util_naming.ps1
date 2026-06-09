$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$teamUtilPath = Join-Path $repoRoot 'src\main\java\com\hepdd\gtmthings\utils\TeamUtil.java'
$teamUtil = Get-Content -Raw -Path $teamUtilPath

if ($teamUtil -cnotmatch 'public static Component getName\(Player player\)') {
    throw 'TeamUtil should expose Java-style getName(Player).'
}

if ($teamUtil -cnotmatch 'public static Component getName\(Level level, UUID playerUUID\)') {
    throw 'TeamUtil should expose Java-style getName(Level, UUID).'
}

if ($teamUtil -cnotmatch 'public static Component GetName\(Player player\)[\s\S]*return getName\(player\);') {
    throw 'Legacy GetName(Player) should remain as a compatibility delegate.'
}

if ($teamUtil -cnotmatch 'public static Component GetName\(Level level, UUID playerUUID\)[\s\S]*return getName\(level, playerUUID\);') {
    throw 'Legacy GetName(Level, UUID) should remain as a compatibility delegate.'
}

if ($teamUtil -cnotmatch '\.filter\(team -> team\.getMembers\(\)\.contains\(playerUUID\)\)\s*\.findFirst\(\)') {
    throw 'Client FTB Teams lookup should keep the legacy first matching team order.'
}

if ($teamUtil -cmatch '\.filter\(Team::isPartyTeam\)\s*\.findFirst\(\)') {
    throw 'Client FTB Teams lookup should not skip earlier non-party matches in a compatibility refactor.'
}

$sourceFiles = Get-ChildItem -Path (Join-Path $repoRoot 'src\main\java') -Recurse -Filter '*.java'
foreach ($file in $sourceFiles) {
    if ($file.FullName -eq (Resolve-Path $teamUtilPath).Path) {
        continue
    }
    $source = Get-Content -Raw -Path $file.FullName
    if ($source -cmatch 'TeamUtil\.GetName|import static com\.hepdd\.gtmthings\.utils\.TeamUtil\.GetName|(?<!public static Component )GetName\(') {
        throw "GTMThings internal source should use TeamUtil.getName instead of GetName: $($file.FullName)"
    }
}

Write-Host 'TeamUtil naming regression check passed.'
