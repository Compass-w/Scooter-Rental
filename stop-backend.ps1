param(
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'

$pattern = "LISTENING\s+(\d+)$"
$lines = netstat -ano | Select-String ":$Port"
$pids = @()

foreach ($line in $lines) {
    $text = $line.ToString().Trim()
    if ($text -match $pattern) {
        $pids += [int]$Matches[1]
    }
}

$pids = @($pids | Select-Object -Unique)

if ($pids.Count -eq 0) {
    Write-Host "No listening process found on port $Port." -ForegroundColor Yellow
    exit 0
}

foreach ($pidValue in $pids) {
    Write-Host "Stopping PID $pidValue on port $Port ..." -ForegroundColor Cyan
    Stop-Process -Id $pidValue -Force
}

Write-Host "Port $Port has been released." -ForegroundColor Green
