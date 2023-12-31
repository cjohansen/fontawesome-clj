(ns fontawesome.ingest
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [hickory.core :as hiccup]
            [fontawesome.icons :as icons]))

(defn to-hiccup [markup]
  (-> markup
      hiccup/parse
      hiccup/as-hiccup
      first
      last
      last
      (update-in [1] #(-> % (dissoc :viewbox) (assoc :viewBox (:viewbox %))))))

(defn ensure-dir! [^String dir]
  (.mkdirs (io/file dir)))

(defn ensure-parent-dir! [^String path]
  (ensure-dir! (.getParent (io/file path))))

(defn convert-icon [base-dir out-dir path]
  (let [style (first (re-find #"^([^/]+)" path))
        out-path (str/replace path (re-pattern (str "-" style ".svg")) ".svg")
        target (str out-dir "/" (str/replace out-path #"\.svg$" ".edn"))
        hiccup (cond-> (to-hiccup (slurp (str base-dir "/" path)))
                 (re-find #"fill\/" path) (assoc-in [1 :fill] "currentColor"))]
    (ensure-parent-dir! target)
    (spit target (str/replace (pr-str hiccup) #":opacity \"0.2\"" ":opacity \"0.2\" :fill \"currentColor\""))))

(defn convert-icons [base-dir out-dir]
  (doseq [file (->> (file-seq (io/file base-dir))
                    (map #(.getPath %))
                    (filter #(re-find #"\.svg$" %))
                    (map #(str/replace % (str base-dir "/") "")))]
    (convert-icon base-dir out-dir file)))

(defn -main [base-dir]
  (convert-icons base-dir (str "resources/" icons/base-path)))
