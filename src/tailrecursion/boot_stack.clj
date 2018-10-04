(ns tailrecursion.boot-stack
  {:boot/export-tasks true}
  (:require
    [boot.core :as boot]
    [boot.pod  :as pod]
    [boot.util :as util]))

(def ^:private deps
  '[[cheshire                                  "5.8.1"]
    [com.amazonaws/aws-java-sdk-cloudformation "1.11.419"]])

(defn- warn-deps [deps]
  (let [conflict (delay (util/warn "Overriding project dependencies, using:\n"))]
    (doseq [dep deps]
      (when (pod/dependency-loaded? dep)
        @conflict
        (util/warn "• %s\n" (pr-str dep))))))

(defn- pod-env [deps]
  (let [dep-syms (->> deps (map first) set)]
    (warn-deps deps)
    (-> (dissoc pod/env :source-paths)
        (update :dependencies #(remove (comp dep-syms first) %))
        (update :dependencies into deps))))

(boot/deftask create
  [n stack NAME            str "Name of the stack"
   r region REGION         str "AWS Region"
   a access-key ACCESS_KEY str "AWS Access Key"
   s secret-key SECRET_KEY str "AWS Secret Key"
   t template TMPL         edn "AWS CloudFormation template."]
  (let [pod (pod/make-pod (pod-env deps))]
    (boot/with-pre-wrap fileset
      (util/info "Creating stack %s in region %s...\n" stack region)
      (pod/with-call-in pod
        (tailrecursion.boot-stack.client/create-stack! ~*opts*))
      fileset)))

(boot/deftask delete
  [n stack NAME            str "Name of the stack"
   r region REGION         str "AWS Region"
   a access-key ACCESS_KEY str "AWS Access Key"
   s secret-key SECRET_KEY str "AWS Secret Key"]
  (let [pod (pod/make-pod (pod-env deps))]
    (boot/with-pre-wrap fileset
      (util/info "Deleting stack %s in region %s...\n" stack region)
      (pod/with-call-in pod 
        (tailrecursion.boot-stack.client/delete-stack! ~*opts*))
      fileset)))
