(ns fontawesome.import
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [fontawesome.icons :as fontawesome]
            [hickory.core :as hiccup])
  (:import (java.io ByteArrayInputStream FileInputStream StringWriter)
           (java.util.zip ZipInputStream)))

(defn fetch-zip-archive [url]
  (ByteArrayInputStream. (:body (client/get url {:as :byte-array}))))

(defn get-zip-file-archive [path]
  (FileInputStream. (io/file path)))

(defn get-zip-entry-content [{:keys [archive]}]
  (let [sw (StringWriter.)
        ^java.io.Reader r (io/reader archive)]
    (io/copy r sw)
    (.toString sw)))

(defn consume-zip [f archive-in]
  (with-open [zip-in (ZipInputStream. archive-in)]
    (loop []
      (when-let [entry (.getNextEntry zip-in)]
        (f {:path (.getName entry)
            :entry entry
            :archive zip-in})
        (.closeEntry zip-in)
        (recur)))))

(defn get-icon-style [path]
  (first (take-last 2 (str/split path #"/"))))

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

(defn export-icon [path markup dir]
  (let [[style file-name] (take-last 2 (str/split path #"/"))
        target (str dir "/" style "/" (-> file-name
                                          (str/replace #"^(\d)" "_$1")
                                          (str/replace #"\.svg$" ".edn")))]
    (ensure-parent-dir! target)
    (spit target (to-hiccup markup))))

(defn export-icons [zip-in target]
  (consume-zip
   (fn [entry]
     (when (re-find #"^(?!fontawesome-.+[/\//]sprites).+\.svg$" (:path entry))
       (export-icon (:path entry) (get-zip-entry-content entry) target)))
   zip-in))

(defn download-icons [version]
  (println "Fetching FontAwesome" version "icons")
  (-> (format "https://use.fontawesome.com/releases/v%s/fontawesome-free-%s-desktop.zip"
              version version)
      fetch-zip-archive))

(defn -main [& opt]
  (let [[mode base-dir & args] opt
        archive (case mode
                  ":download" (apply download-icons args)
                  ":import" (apply get-zip-file-archive args)
                  (do
                    (println "To download the free icon package into your resources directory, use the")
                    (println ":download keyword, specify a directory on your classpath, typically `resources`,")
                    (println "and a version to download (refer to the FontAwesome website):")
                    (println)
                    (println "clojure -m fontawesome.import :download resources 6.4.2")
                    (println)
                    (println "If you have a commercial license, use the :import keyword, specify a")
                    (println "directory on your classpath, typically `resources`, and the path to")
                    (println "the downloaded zip-file:")
                    (println)
                    (println "clojure -m fontawesome.import :import resources ~/Downloads/fontawesome-pro-6.4.2-desktop.zip")
                    (println)
                    (System/exit 1)))]
    (println "Extracting icons to" base-dir)
    (export-icons archive (str base-dir "/" fontawesome/base-path))))

(comment

  (-main "dev-resources" "import" "/tmp/lul3.zip")

  (def icon
    {:path "fontawesome-free-6.4.2-desktop/svgs/solid/angle-right.svg"
     :svg "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 320 512\"><!--! Font Awesome Free 6.4.2 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free (Icons: CC BY 4.0, Fonts: SIL OFL 1.1, Code: MIT License) Copyright 2023 Fonticons, Inc. --><path d=\"M278.6 233.4c12.5 12.5 12.5 32.8 0 45.3l-160 160c-12.5 12.5-32.8 12.5-45.3 0s-12.5-32.8 0-45.3L210.7 256 73.4 118.6c-12.5-12.5-12.5-32.8 0-45.3s32.8-12.5 45.3 0l160 160z\"/></svg>"})

  (export-icon (:path icon) (:svg icon) "dev-resources")

  (to-hiccup (:svg icon))

  (get-icon-style (:path icon))

  (def version "6.4.2")
  (def url (format "https://use.fontawesome.com/releases/v%s/fontawesome-free-%s-desktop.zip" version version))

  (->> (fetch-zip-archive url)
       (consume-zip
        (fn [entry]
          (when (re-find #"angle-right\.svg$" (:path entry))
            (prn "Yay!" (:path entry) (get-zip-entry-content entry)
                 )))))

)
