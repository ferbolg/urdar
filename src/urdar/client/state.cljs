(ns urdar.client.state
  (:require [urdar.client.pubsub :as p]))

;;; TODO bookmarks-to-fetch should be based on screen height
(def state (atom {:bookmarks-fetched 0 :bookmarks-to-fetch 20 :tag nil
                  :bookmarks-id 0}))

(defn bookmark-fetched! [_]
  (swap! state update-in [:bookmarks-fetched] inc))

(defn bookmark-removed! [_]
  (swap! state update-in [:bookmarks-fetched] dec))

(defn set-bookmarks-to-fetch [n]
  (swap! state assoc :bookmarks-to-fetch n))

(defn tag-selected? [tag] (= (:tag @state) tag))

(defn set-tag! [{:keys [tag]}]
  ;; TODO should be one swap! call
  (swap! state assoc :tag tag)
  (swap! state assoc :bookmarks-fetched 0))

(defn unset-tag! [{:keys [tag]}]
  (let [{tag-selected :tag bookmarks-fetched :bookmarks-fetched} @state]
    (when (and (or (nil? tag) (and tag (= tag-selected tag)))
               (< bookmarks-fetched 1))
      (p/publish-tag-changed (p/->TagChangedEvent nil)))))

(defn reset-id [] (swap! state assoc :bookmarks-id 0))

;;; TODO incorrect - will produce identical IDs when page is refreshed
(defn generate-id [link]
  (:bookmarks-id (swap! state update-in [:bookmarks-id] inc)))
