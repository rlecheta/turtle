turtle
======

O jogo Running Turtle (https://play.google.com/store/apps/details?id=br.android.tarta) está publicado no Android Market.

Ele foi feito em 2010 e nunca mais trabalhei nele.

Como não pretendo mais trabalhar no jogo, estou liberando o fonte, para auxiliar quem deseja estudar como ele foi feito.

----------------------------------

1) Jogo publicado no Android Market.

https://play.google.com/store/apps/details?id=br.android.tarta

2) Engine feita com base no livro: Developing Games in Java (David Brackeen)

3) O capítulo 5 é extramente interessante, e é a base da engine.

Paralax scrolling, tile-based maps, gravity, jumping, and bounding-box collisions.

4) Mapas feitos em txt, cada caracter é mapeado para um objeto ou personagem

https://github.com/rlecheta/turtle/blob/master/Tarta/src-etc/mapas/map1.txt

5) Utiliza a Game API de J2ME que foi portada para Android.

Inicialmente peguei a versão daqui, e depois fiz modificões.

http://code.google.com/p/sporksoft/source/browse/#svn/trunk/com/sporksoft/game

O gameapi.jar e fontes estão na pasta libs do projeto.

A vantagem da Game API é que os cenários e personagems podem ser criados facilmente com as classes de TileManager e Sprite, 
e ainda a parte de movimento e tratamento de colisões está tudo implementado.

6) A classe GameManager é onde está o game loop do jogo, é por lá que você deve começar.

Não devo mais me lembrar do fonte, então bom proveito. Nada que uma debugada você não consiga entender.

abraços
Ricardo Lecheta
http://www.ricardolecheta.com.br
http://www.livroandroid.com.br
