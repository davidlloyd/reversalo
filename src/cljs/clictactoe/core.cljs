(ns clictactoe.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [clictactoe.handlers]
              [clictactoe.subs]
              [clictactoe.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
