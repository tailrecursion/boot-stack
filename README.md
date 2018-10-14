# boot-stack

a boot task for deploying aws cloud formation templates

[](dependency)
```clojure
[tailrecursion/boot-stack "0.1.0"] ;; latest release
```
[](/dependency)

## overview

the `create` task provisions a new cloudfront stack from a template described
by clojure data structures, instead of json and yaml, so it may be conveniently
manipulated by a boot build.

conversely, the `delete` task tears it down.
