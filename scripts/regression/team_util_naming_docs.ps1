$ErrorActionPreference = "Stop"

$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$src = Join-Path $root "src\main\java"
$file = Join-Path $src "com\hepdd\gtmthings\utils\TeamUtil.java"
$text = Get-Content -Raw -LiteralPath $file

if ($text -notmatch "public\s+static\s+Component\s+getName\(Player\s+player\)") {
    throw "TeamUtil should expose getName(Player)"
}
if ($text -notmatch "public\s+static\s+Component\s+getName\(Level\s+level,\s+UUID\s+playerUUID\)") {
    throw "TeamUtil should expose getName(Level, UUID)"
}
if ($text -notmatch "@Deprecated[\s\S]*public\s+static\s+Component\s+GetName\(Player\s+player\)") {
    throw "Legacy GetName(Player) should be deprecated"
}
if ($text -notmatch "@Deprecated[\s\S]*public\s+static\s+Component\s+GetName\(Level\s+level,\s+UUID\s+playerUUID\)") {
    throw "Legacy GetName(Level, UUID) should be deprecated"
}
if ($text -notmatch "/\*\*[\s\S]*getTeamUUID") {
    throw "getTeamUUID should have Javadoc"
}

$legacyCalls = rg -n "GetName\(" $src
if ($LASTEXITCODE -eq 0) {
    $outsideTeamUtil = $legacyCalls -split "`n" | Where-Object { $_ -and ($_ -notmatch [regex]::Escape($file)) }
    if ($outsideTeamUtil) {
        throw "Production code still calls legacy GetName:`n$($outsideTeamUtil -join "`n")"
    }
} elseif ($LASTEXITCODE -ne 1) {
    throw "rg failed while checking legacy GetName calls"
}

Write-Host "TeamUtil naming regression passed."
