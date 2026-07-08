```powershell
Param(
    [switch]$RunBuild
)

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $projectRoot

$targets = @(
    "src\main\resources\templates\nuevo-venta-fixed.html",
    "src\main\resources\templates\nuevo-pedido.html",
    "src\main\resources\templates\confirmacion-pedido.html",
    "src\main\resources\templates\historial-pedidos.html",
    "build\resources\main\templates\nuevo-venta-fixed.html",
    "build\resources\main\templates\nuevo-pedido.html",
    "build\resources\main\templates\confirmacion-pedido.html",
    "build\resources\main\templates\confirmacion-venta.html",
    "src\main\java\edu\pe\utp\marcodesarrolloweb\ferrovoz\model\Pedido.java",
    "src\main\java\edu\pe\utp\marcodesarrolloweb\ferrovoz\dto\PedidoDTO.java",
    "src\main\java\edu\pe\utp\marcodesarrolloweb\ferrovoz\controller\PedidoController.java",
    "src\main\java\edu\pe\utp\marcodesarrolloweb\ferrovoz\service\PedidoService.java",
    "src\main\java\edu\pe\utp\marcodesarrolloweb\ferrovoz\repository\PedidoRepository.java"
)

$existing = $targets | Where-Object { Test-Path $_ }

if ($existing.Count -eq 0) {
    Write-Host "No se encontraron archivos objetivo." -ForegroundColor Yellow
    exit 0
}

Write-Host "Archivos eliminados:" -ForegroundColor Cyan

foreach ($f in $existing) {
    try {
        Remove-Item -Force -LiteralPath $f
        Write-Host " - $f"
    }
    catch {
        Write-Warning "No se pudo borrar $f"
    }
}

if ($RunBuild) {
    if (Test-Path ".\gradlew.bat") {
        Write-Host "Ejecutando gradle clean..." -ForegroundColor Cyan
        & .\gradlew.bat clean
    }
}

Write-Host "Limpieza completada." -ForegroundColor Green
```
