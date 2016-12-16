# reversalo

PLAY ON [here](http://www.touchsoftware.cc/reversalo/index.html)

An othello-like board that has some game logic. It puts up a decent fight but is still beatable.

Built on [re-frame](https://github.com/Day8/re-frame) - a reagent template for ReactJS on clojurescript.

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run tests:

```
lein clean
lein cljsbuild auto test
```

## Production Build

```
lein clean
lein cljsbuild once min
```
