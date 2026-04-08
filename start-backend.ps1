param(
    [int]$Port = 8080,
    [switch]$SkipTests = $true
)

$ErrorActionPreference = 'Stop'

function Get-ListeningPids {
    param([int]$TargetPort)

    $pattern = "LISTENING\s+(\d+)$"
    $matches = netstat -ano | Select-String ":$TargetPort"

    foreach ($line in $matches) {
        $text = $line.ToString().Trim()
        if ($text -match $pattern) {
            [int]$Matches[1]
        }
    }
}

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $repoRoot 'backend'

if (-not (Test-Path $backendDir)) {
    throw "Backend directory not found: $backendDir"
}

$listeningPids = @(Get-ListeningPids -TargetPort $Port | Select-Object -Unique)
if ($listeningPids.Count -gt 0) {
    Write-Host "Port $Port is already in use by PID(s): $($listeningPids -join ', ')." -ForegroundColor Yellow
    Write-Host "The backend is probably already running, so you do not need to start another one." -ForegroundColor Yellow
    Write-Host "If you want to restart it, run .\stop-backend.ps1 -Port $Port first." -ForegroundColor Yellow
    exit 0
}

Push-Location $backendDir
try {
    $args = @()
    if ($SkipTests) {
        $args += '-DskipTests'
    }
    $args += 'spring-boot:run'

    Write-Host "Starting backend on port $Port from $backendDir ..." -ForegroundColor Cyan
    & mvn @args
} finally {
    Pop-Location
}
