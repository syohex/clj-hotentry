(ns hotentry.core
  (:require [clojure.tools.cli :as cli]
            [feedparser-clj.core :as feedparser]) )

(def ^:private options
  [["-t" "--threshold NUM" "threshold of bookmarks"
    :default 3]
   ["-l" "--limit" "limit of printing entries"
    :default 10]
   ["-h" "--help"]])

(defn- hotentry-url [keyword threshould]
  (format "http://b.hatena.ne.jp/search/tag?q=%s&users=%d&mode=rss"
          keyword threshould))

(defn- parse-rss [url]
  (let [feed (feedparser/parse-feed url)]
    (map #(select-keys % [:title :link]) (:entries feed))))

(defn- format-entries [entries]
  (map-indexed (fn [i e]
                 (format "%2d: %s"
                         (inc i) (:title e)))
               entries))

(defn -main [& args]
  (let [parsed (cli/parse-opts args options)
        opt (:options parsed)
        key (first (:arguments parsed))
        url (hotentry-url key (:threshold opt))
        entries (parse-rss url)]
    (doseq [entry (take (:limit opt) (format-entries entries))]
      (println entry))))
