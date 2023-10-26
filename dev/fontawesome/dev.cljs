(ns fontawesome.dev
  (:require [fontawesome.icons :as icons :refer-macros [get-icon-ids load-all-icons]]
            [portfolio.dumdom :refer-macros [defscene configure-scenes]]
            [portfolio.ui :as ui]))

(configure-scenes
 {:title "FontAwesome Icons"})

(load-all-icons)

(defn render-icon [icon attr]
  [:div {:title (str icon)
         :onClick (fn [e]
                    (js/navigator.clipboard.writeText (str icon)))}
   (icons/render icon attr)])

(defscene regular-icons
  [:div {:style {:display "flex"
                 :flex-wrap "wrap"
                 :gap 20}}
   (->> (get-icon-ids)
        (filter (comp #{"fontawesome.regular"} namespace))
        (map #(render-icon % {:size 32 :color "#1181f9"})))])

(defscene solid-icons
  [:div {:style {:display "flex"
                 :flex-wrap "wrap"
                 :gap 20}}
   (->> (get-icon-ids)
        (filter (comp #{"fontawesome.solid"} namespace))
        (map #(render-icon % {:size 32 :color "red"})))])

(defscene brand-icons
  [:div {:style {:display "flex"
                 :flex-wrap "wrap"
                 :gap 20}}
   (->> (get-icon-ids)
        (filter (comp #{"fontawesome.brands"} namespace))
        (map #(render-icon % {:size 32 :color "#61c093"})))])

(ui/start!)
