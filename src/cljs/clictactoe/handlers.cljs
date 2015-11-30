(ns clictactoe.handlers
    (:require [re-frame.core :as re-frame]
              [clictactoe.db :as db]))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))


(re-frame/register-handler
 :game
 (fn  [db [_ value]]
   (assoc db :game value)))
