Kitten
======

A simple compiler for a Java-like object-oriented language


NOTE SULL'UTILIZZO:

- Il target run-compiled-test-code compila il programma a partire dal file indicato in "build.properties" come "kitten.example" e genera per ogni classe C trovata (che presenta test) un relativo file CTest: il CTest eseguito è quello relativo alla classe indicata come "kitten.example.test" in "build.properties".

- Il target run-java-bytecode-generator mostrerà per ogni classe C con test un messaggio di avviso del tipo CTest.kit::: Cannot find "CTest.kit"; ciò è dovuto al fatto che nella traduzione degli assert si costruisce un ClassType non esistente, per poter usare una FieldSignature quando l'assert sarà all'interno
della CTest. 
(L'uso di un campo è la soluzione a cui siamo giunti per rispettare le indicazioni secondo cui i test in CTest dovevano essere metodi void con parametro la sola classe testata)