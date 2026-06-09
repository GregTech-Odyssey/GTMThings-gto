$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$sourcePath = Join-Path $repoRoot 'src\main\java\com\hepdd\gtmthings\utils\FormatUtil.java'
$source = Get-Content -Raw $sourcePath

if ($source -cnotmatch 'String\.format\(Locale\.ROOT,') {
    throw 'formatNumber should use Locale.ROOT for stable decimal separators.'
}

foreach ($pattern in @(
        'spaceLength <= 0',
        'splitChar == null \|\| splitChar\.isEmpty\(\)',
        'splitWidth <= 0',
        'Math\.max\(0, spacerCount - 2\)'
    )) {
    if ($source -cnotmatch $pattern) {
        throw "Expected getSpacer guard or clamp matching pattern: $pattern"
    }
}

Write-Host 'FormatUtil guard regression check passed.'
