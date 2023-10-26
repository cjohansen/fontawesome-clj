# FontAwesome SVG icons as Hiccup

This library provides tooling to use the icons in the [Fontawesome Icons
package](https://fontawesome.com/) as hiccup, usable from both Clojure and
ClojureScript.

## Install

With tools.deps:

```clj
no.cjohansen/fontawesome-clj {:mvn/version "2023.10.26"}
```

With Leiningen:

```clj
[no.cjohansen/fontawesome-clj "2023.10.26"]
```

### Download FontAwesome

Due to FontAwesome's commercial license, the icons are not shipped with the
library. Instead, you will import the resources to your classpath. You can
either do this as a build step, or commit the resources to your project - so
long as you don't distribute your project in a way that conflicts with the
FontAwesome license (e.g. it's open source).

fontawesome-clj needs a couple dependencies to import icons that are not
necessary at runtime. To reduce the number of dependencies at runtime, you will
have to provide those dependencies while importing:

To use the free distribution:

```sh
clojure -Sdeps "{:deps {no.cjohansen/fontawesome {:mvn/version \"2023.10.26\"} \
                        clj-http/clj-http {:mvn/version \"3.12.3\"} \
                        hickory/hickory {:mvn/version \"0.7.1\"}}}" \
  -m fontawesome :download resources 6.4.2
```

This will install version 6.4.2 icons into your `resources` directory.

To use a pro distribution you must first download the zip file, then import it:

```sh
clojure -Sdeps "{:deps {no.cjohansen/fontawesome {:mvn/version \"2023.10.26\"} \
                        clj-http/clj-http {:mvn/version \"3.12.3\"} \
                        hickory/hickory {:mvn/version \"0.7.1\"}}}" \
  -m fontawesome :import resources 6.4.2~/Downloads/fontawesome-pro-6.4.2-desktop.zip
```

## Usage from Clojure

Usage from Clojure is straight forward:

```clj
(require '[fontawesome.icons :as icons])

(icons/render :fontawesome.regular/bell)

;;=> [:svg
;;    {:xmlns "http://www.w3.org/2000/svg"
;;     :viewBox "0 0 448 512"
;;     :style {:display "inline-block"
;;             :line-height "1"}}
;;    "<!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free (Icons: CC BY 4.0, Fonts: SIL OFL 1.1, Code: MIT License) Copyright 2023 Fonticons, Inc. -->"
;;    [:path {:d "M224 0c-17.7 0-32 14.3-32 32V49.9C119.5 61.4 64 124.2 64 200v33.4c0 ..."}]]
```

More on the `render` function below.

## Usage from ClojureScript

The Fontawesome icons package contains thousands of icons. You probably do not
want to include all of them in your build. To work around this, the library
provides a macro that "installs" an icon into your build. After you've installed
it, you can render it as much as you want.

```clj
(require '[fontawesome.icons :as icons])

(icons/render (icons/icon :fontawesome.brands/apple))
```

This will both pull the Apple logo icon into your build and render it.
`icons/icon` needs only be called once per unique id. It returns the keyword, so
can be used where you build data - it doesn't have to sit in your rendering
code:

```clj
(def data
  {:name "Christian"
   :icon (icons/icon :fontawesome.regular/user)})

(icons/render (:icon data) {:size 32})
```

More on the `render` function below.

You can also install icons in a separate namespace and forget about `icons/icon`
in the rest of your application. The important part is that
`fontawesome.icons/icon` is called once for every icon you intend to use, and
that it is called with the static keyword - it is a macro, and cannot
dynamically look up refs. This will **not** work:

```clj
;; Doesn't work, don't do it!

(for [id [:fontawesome.regular/building
          :fontawesome.solid/building
          :fontawesome.regular/chess-pawn
          :fontawesome.solid/chess-pawn]]
  (fontawesome.icons/icon id))
```

## Icon keywords

Icons are identified with a keyword. The keyword has the following anatomy:

```clj
:fontawesome.<group>/<id>
```

`group` is one of:

- `regular`
- `brand`
- `solid`

`id` is the icons id. Use the [Fontawesome icons
website](https://fontawesome.com/icons/) to find icons.

## The render function

The render function takes two arguments:

```clj
(render id {:size :color :style :class})
```

All the map options are optional.

- `size` is a number that is used for the icons width and height
- `color` is the icon's color. Icons use `currentColor`, so you can also set
  color with CSS in parent elements.
- `style` is a map of styles for the `svg` element
- `class` is either a compatible format for specifying CSS classes that your
  rendering library supports (usually either an array of strings or a
  space-separated list)

The remaining map is merged into the SVG element's attributes, e.g.:

```clj
(require '[fontawesome.icons :as icons])

(icons/render (icons/icon :fontawesome.regular/file-pdf) {:on-click (fn [e] ,,,)})

;;=> [:svg {:on-click (fn [e] ,,,)
;;          :viewBox ",,,"
;;          ,,,}
;;    ,,,]
```

## License

Copyright code in this repo © 2023 Christian Johansen

Distributed under the MIT license.

FontAwesome icons use a commercial license, see [the
website](https://fontawesome.com/).
