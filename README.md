# LGMatcherJsonExporter

[LGMatcher](https://github.com/kusumotolab/LGMatcher)を利用。
`Gradle 4.10.3`(古い)が、新しいJavaではセキュリティ上禁止されている内部的な機能を使おうとして、Java側からブロックされていたため、GradleとJavaのバージョンは引き上げている。
___

LGMatcherSampleを元に作成。

## setup

jdk17でのみコンパイル可能
```
./gradlew shadowJar
```

## how to use 
```
java -jar LGMatcherJsonExporter-1.0-all.jar [出力先ファイル] [src] [dst]
```

## setup for Eclipse(もう不要)
eclipse上でパッケージを右クリック -> `プロパティ` -> `javaのビルドパス` -> `プロジェクトタブ` -> LGMatcherを追加。
