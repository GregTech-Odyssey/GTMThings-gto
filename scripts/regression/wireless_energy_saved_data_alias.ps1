$ErrorActionPreference = "Stop"

$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$src = Join-Path $root "src\main\java"
$alias = Join-Path $src "com\hepdd\gtmthings\data\WirelessEnergySavedData.java"
$legacy = Join-Path $src "com\hepdd\gtmthings\data\WirelessEnergySavaedData.java"

if (-not (Test-Path $alias)) {
    throw "Missing correctly spelled WirelessEnergySavedData"
}

$aliasText = Get-Content -Raw -LiteralPath $alias
if ($aliasText -notmatch "package\s+com\.hepdd\.gtmthings\.data;") {
    throw "WirelessEnergySavedData package declaration is wrong or missing"
}
if ($aliasText -notmatch "class\s+WirelessEnergySavedData\s+extends\s+WirelessEnergySavaedData") {
    throw "WirelessEnergySavedData should be a correctly spelled facade over the binary-compatible legacy owner"
}

$legacyText = Get-Content -Raw -LiteralPath $legacy
if ($legacyText -notmatch "package\s+com\.hepdd\.gtmthings\.data;") {
    throw "Legacy WirelessEnergySavaedData package declaration is wrong or missing"
}
if ($legacyText -notmatch "@Deprecated") {
    throw "Legacy WirelessEnergySavaedData must remain deprecated for compatibility"
}
if ($legacyText -notmatch "class\s+WirelessEnergySavaedData\s+extends\s+SavedData") {
    throw "Legacy WirelessEnergySavaedData should keep owning the SavedData implementation for binary compatibility"
}
if ($legacyText -notmatch "public\s+static\s+WirelessEnergySavaedData\s+INSTANCE") {
    throw "Legacy WirelessEnergySavaedData must keep the binary-compatible INSTANCE field"
}

$badImports = rg -n "import com\.hepdd\.gtmthings\.data\.WirelessEnergySavaedData" $src
if ($LASTEXITCODE -eq 0) {
    throw "GTMThings production code still imports WirelessEnergySavaedData:`n$badImports"
}
if ($LASTEXITCODE -ne 1) {
    throw "rg failed while checking legacy wireless energy imports"
}

Write-Host "Wireless energy saved data alias regression passed."
