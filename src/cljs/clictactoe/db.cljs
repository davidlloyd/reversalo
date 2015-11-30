(ns clictactoe.db
	(:require [clictactoe.game :as game]))

(def default-db
  {:name "Reversalo"
   :game (game/new-game 8)})
