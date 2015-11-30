(ns clictactoe.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [clictactoe.game :as game]))

(defn title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/v-box
       :align :center
       :children [
        [re-com/title
         :label (str "TOUCH" )
         :level :level2]
        [re-com/title
         :label (str "" @name)
         :level :level1]]])))


(defn blank
  [game i j background-color]
  [:rect
   {:width 0.9
    :height 0.9
    :fill background-color
    :x (+ 0.05 i)
    :y (+ 0.05 j)
    :on-click
    (fn blank-click [e]
      (game/player-move game i j))}])

(defn circle [i j]
  [:circle
   {:r 0.35
    :stroke "green"
    :stroke-width 0.12
    :fill "none"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn cross [i j]
  [:g {:stroke "darkred"
       :stroke-width 0.4
       :stroke-linecap "round"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.3)")}
   [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
   [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])

(defn black-disc [i j]
  [:circle
   {:r 0.3
    :fill "black"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn white-disc [i j]
  [:circle
   {:r 0.3
    :fill "white"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn game-board []
  (let [game (re-frame/subscribe [:game])
        board-size (:board-size @game)
        background-color (:background-color @game)]
    (fn []
      (.log js/console "game:" @game)
      #_(println board-size)
      #_(println background-color)
      (->
        [:svg
          {:view-box (str "0 0 " board-size " " board-size)
           :width 500
           :height 500}
          ]
         (into
          (for [i (range board-size)
                j (range board-size)]
            [blank @game i j background-color]))
         (into
          (for [i (range board-size)
                j (range board-size)
                :let [x (get-in (:board @game) [j i])]
                :when (not= " " x)]
            (case x
              "B" [black-disc i j]
              "W" [white-disc i j]
              "C" [cross i j]
              "P" [circle i j])))))))

(defn main-panel []
  (fn []
    [re-com/v-box
       :align :center
     :height "100%"
     :children [[title]
                [game-board]]]))
