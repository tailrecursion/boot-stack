(ns tailrecursion.boot-stack.client
  (:require
    [cheshire.core :refer [generate-string]])
  (:import
    [com.amazonaws.auth                          AWSStaticCredentialsProvider BasicAWSCredentials]
    [com.amazonaws.services.cloudformation       AmazonCloudFormationClientBuilder]
    [com.amazonaws.services.cloudformation.model CreateStackRequest DeleteStackRequest]))

(defn client [acc-key sec-key region]
  (let [creds (AWSStaticCredentialsProvider. (BasicAWSCredentials. acc-key sec-key))]
    (-> (AmazonCloudFormationClientBuilder/standard)
        (.withCredentials creds)
        (.withRegion region)
        (.build)
        (delay))))

(defn creation [stack template]
  (-> (CreateStackRequest.)
      (.withStackName stack)
      (.withTemplateBody (generate-string template))))

(defn deletion [stack]
  (-> (DeleteStackRequest.)
      (.withStackName stack)))

(defn create-stack! [{:keys [access-key secret-key region stack template]}]
  (let [client  @(client access-key secret-key region)
        creation (creation stack template)]
    (.toString (.createStack client creation))))

(defn delete-stack! [{:keys [access-key secret-key region stack]}]
  (let [client  @(client access-key secret-key region)
        deletion (deletion stack)]
    (.toString (.deleteStack client deletion))))
