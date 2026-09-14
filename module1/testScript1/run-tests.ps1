$ErrorActionPreference = 'Stop'

if (-not $env:JAVA_HOME) {
    $jdkRoot = Join-Path $env:USERPROFILE '.jdks'
    if (Test-Path -LiteralPath $jdkRoot) {
        $jdk = Get-ChildItem -LiteralPath $jdkRoot -Directory |
            Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin\javac.exe') } |
            Sort-Object LastWriteTime -Descending |
            Select-Object -First 1
        if ($jdk) {
            $env:JAVA_HOME = $jdk.FullName
        }
    }
}

$mavenRepo = Join-Path $env:USERPROFILE '.m2\repository'
$userHomeOption = '-Duser.home="' + $env:USERPROFILE + '"'
$env:MAVEN_OPTS = (($env:MAVEN_OPTS, $userHomeOption) -join ' ').Trim()
& "$PSScriptRoot\mvnw.cmd" "-Dmaven.repo.local=$mavenRepo" '-Dtest=com.xiaobaitiao.springbootinit.module1.*Test' test
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
