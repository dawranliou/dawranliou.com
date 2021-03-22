+++
title = "The distinction between Reagent component and React component"
authors = "Daw-Ran Liou"
+++

At work, I rarely have to think about the distinction between a Reagent
component and a React component since Reagent does such a good job providing a
coherent and simple API to use. (Plus, we don't use any extern JS component
libraries.)

I came across the [StackOverflow question] the other day. While researching for
the answer, it occurred to me that the simplicity of Reagent API can sometimes
trick people up. The focus of this article isn't so much about the original SO
question, though I will use the example in the post, but more about sharing some
easy-to-overlook information for the Reagent users.

## The distinction

[React components] are classes that extends the `React.Component`
class. Example:

```javascript
// JSX style
class Welcome extends React.Component {
  render() {
    return <h1>Hello, {this.props.name}</h1>;
  }
}

function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}

// JS style
class Hello extends React.Component {
  render() {
    return React.createElement('div', null, `Hello ${this.props.toWhat}`);
  }
}
```

As oppose to React components, Reagent components in the most simplified form
(also called as Form-1 component) are just Clojure functions that returns Clojure data in
[Hiccup] style.

```clojure
(defn welcome [props]
  [:h1 {} (:name props)])
```

> There are other ways to define a Reagent component as described in the
> official documentation on [Creating Reagent Components].

Although this may seem counter-intuitive, since I'm already calling it
"component", the Reagent component above is just a pure Clojure function and
**there is no more hidden magic behind it**. You can "invoke" it like any other
functions in Clojure, and it'll return a Hiccup markup without any surprise:

```clojure
(welcome {:name "Steve"})
;; => [:hi {} "Steve"]
```

The returned markup data is plain Clojure data, no rendering, no reactivity, no
magic, just pure Clojure data, persistent and immutable. Please be aware, in a
Reagent app, seldom should you "invoke" the Reagent component like
this. Instead, you should "render" it by explicitly invoking the
`reagent.dom/render` function to render the top-level Reagent component.

## Rendering

What the `reagent.dom/render` function does is:

1. Compile the top level **Reagent** component (and all the nested children
   components) into **React** components,
1. Invoke JS function `ReactDOM.render(...)` with the compiled **React**
   components to render the **DOM** elements.

Or in the form of a flow chart:

```
Reagent       Compile   React        Render
Components    --------> Components   ------->  DOM
(Clojure Fns)           (JS Classes)
```

Most of the time, you don't have to think about this because all components are
Reagent components. Thus it's easy to overlook the Reagent's part in compiling
the Clojure functions into React Components. _(This might be a reason why some
other libraries prefer explicit macros to define a Component like rum's `defc`
or fulcro's `defsc` - to make the compilation explicit to the user.)_  However,
when it comes to interop with React component libraries, we need to start
thinking more clearly about which components are React and which are Reagent.

## Interop with React

The official website has an excellent article about [Interop with React]. Check
it out if you aren't so familiar with it. Read on for yet another example.

## Example: Stylized Material UI Component

Below is the example I worked out for the [StackOverflow question]. The props
value for `:ValueLabelComponent` needs to be a React component because `slider`
is an adapted react class just as the `mui-value-labe` in the example, hence the
function call to `r/reactify-component`.

The real tricky part is that `reagent-material-ui.styles/with-styles` returns a
`reagent.impl.template.NativeWrapper` and `r/reactify-component` cannot handle
it. Therefore, I needed to wrapped it inside another function call to make it
work.

```
(ns example.core
  (:require
   [reagent-material-ui.core.slider :refer [slider]]
   [reagent-material-ui.styles :as styles]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [reagent.impl.util :as rutil]
   ["@material-ui/core/Slider/ValueLabel" :as MuiValueLabel]))

(def mui-value-label
  (r/adapt-react-class (.-default MuiValueLabel)))

(def with-offsets
  (styles/with-styles {:offset {:top 50 :left 50}}))

(defn value-label-with-offsets
  [props]
  [(with-offsets mui-value-label) props])

(defn main []
  [slider
   {:defaultValue        [31 37]
    :valueLabelDisplay   "on"
    :ValueLabelComponent (r/reactify-component
                           ;; Nay
                           #_(with-offsets mui-value-label)
                           ;; Ok
                           #_(fn [props] [(with-offsets mui-value-label) props])
                           ;; Yay
                           value-label-with-offsets)}])

(defn ^{:after-load true, :dev/after-load true}
  mount []
  (rdom/render [main] (js/document.getElementById "app")))

(defn ^:export init []
  (mount))
```

## Epilogue

Spending time reading the Reagent source code was a lot of fun for me. Here I
just want to share a few interesting functions I saw:

`reagent.impl.template` NS:
- `vec-to-elem`: convert a vec into a JS React element via `make-element`
- `make-element`: make a JS React element
- `as-element`
- `valid-tag?`: return true if a tag is a symbol, keyword, string, function, or
  NativeWrapper.

`reagent.impl.util` NS:
- `dash-to-prop-name`: turn kebab-case prop names into camelCase.


[Interop with React]: https://cljdoc.org/d/reagent/reagent/1.0.0/doc/tutorials/interop-with-react

[StackOverflow question]: https://stackoverflow.com/q/66714919

[reagent]: https://github.com/reagent-project/reagent/

[React Components]: https://reactjs.org/docs/react-component.html

[Hiccup]: https://github.com/weavejester/hiccup

[Creating Reagent Components]: https://cljdoc.org/d/reagent/reagent/1.0.0/doc/tutorials/creating-reagent-components
