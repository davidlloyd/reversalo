(ns clictactoe.game
	(:require [re-frame.db :refer [app-db]]
			  [re-frame.core :refer [dispatch]]))

(defn other-player [p]
	(cond
		(= p "W") "B"
		(= p "B") "W"
		(= p "1") "2"
		(= p "2") "1"
		(= p "X") "O"
		(= p "C") "P"
		(= p "P") "C"))

(defn available? [game x y]
	(.log js/console "available?")
	(and (= (:status game) :in-progress)
		(= (get-in (:board game) [y x]) " ")))

(defn capture-line [board x y dx dy player]
  #_(.log js/console " - try capture line " x y dx dy)
  (when-let [opponent (other-player player)]
    (loop [x (+ x dx)
           y (+ y dy)
           found []]
      (cond
        (= opponent (get-in board [y x]))
        (recur (+ x dx)
               (+ y dy)
               (conj found [x y]))

        (= player (get-in board [y x]))
        found

        :else
        nil))))

(defn capture [board x y player]
  #_(.log js/console " - try capture  " x y)
  (seq
   (mapcat
    (fn [[dx dy]]
      (capture-line board x y dx dy player))
    (for [dx [-1 0 1]
          dy [-1 0 1]
          :when (not= 0 dx dy)]
      [dx dy]))))

(defn calc-move [player game x y ]
	#_(.log js/console "calc-move " x y (:board game))
	(if (available? game x y)
		(do
			(.log js/console "calc-move is available " (capture (:board game) x y player ))
			(capture (:board game) x y player)
			)))

(defn othello-move [board x y flips player]
  (reduce
   (fn [b [i j]]
     (assoc-in b [j i] player))
   (assoc-in board [y x] player)
   flips))

(defn can-move? [player game x y]
	#_(.log js/console "can-move?")
	(not (empty? (calc-move player game x y))))

(defn available-moves [{:keys [board-size] :as game} player]
	(let [	m (if (not (empty? (calc-move player game 0 0 ))) [0 0])
			m (if (not (empty? (calc-move player game 0 (dec board-size) ))) [0 (dec board-size)])
			m (if (not (empty? (calc-move player game (dec board-size) 0 ))) [(dec board-size) 0])
			m (if (not (empty? (calc-move player game (dec board-size) (dec board-size) ))) [(dec board-size) (dec board-size)])
			m (if (empty? m) (filter identity
				(for [i (shuffle (range 0 board-size))
					  :let [m (calc-move player game 0 i)]
					  :when (and m (not (empty? m)))]
					[0 i]))
				m)
			m (if (empty? m) (filter identity
				(for [i (shuffle (range 0 board-size))
					  :let [m (calc-move player game i 0)]
					  :when (and m (not (empty? m)))]
					[i 0]))
				m)
			m (if (empty? m) (filter identity
				(for [i (shuffle (range 0 board-size))
					  :let [m (calc-move player game (dec board-size) i)]
					  :when (and m (not (empty? m)))]
					[(dec board-size) i]))
				m)
			m (if (empty? m) (filter identity
				(for [i (shuffle (range 0 board-size))
					  :let [m (calc-move player game i (dec board-size))]
					  :when (and m (not (empty? m)))]
					[i (dec board-size)]))
				m)
			m (if (empty? m) (filter identity
				(for [i (shuffle (range 0 board-size))
					  j (shuffle (range 0 board-size))
					  :let [m (calc-move player game i j)]
					  :when (and m (not (empty? m)))]
					[i j]))
				m)]
		m
	))

(defn draw? [{:keys [computer player] :as game}]
  (and (empty? (available-moves game computer))
       (empty? (available-moves game player))))

(defn did-win? [game ]
	(.log js/console "did-win?")
	(let [{es " " ps (:player game) cs (:computer game)} (frequencies (apply concat (:board game)))]
		#_(.log js/console " - frq : " es (:computer game) cs (empty? (available-moves game (:computer game))) (:player game) ps (empty? (available-moves game (:player game))))
	    (if (draw? game)
    		(if (> ps cs)
    			:player-victory
    			(if (> cs ps)
    				:computer-victory
    				:draw))
			:in-progress)))

(defn player-move [game x y]
	(.log js/console "player-move")
	(let [player (:player game)
		  cm (can-move? player game x y)]
		(.log js/console "CLICK " player cm)
		(if cm
			(let [g (assoc game :board (othello-move (:board game) x y (calc-move player game x y) player ))
				  g (assoc g :status (did-win? g )) ]
				(.log js/console " - status " (:status g))
				(if (= (:status g) :in-progress)
					(let [m (available-moves g (:computer g))
						  x (.log js/console "player board " (:board g))
						  x (.log js/console "avail move:" (first m))
						  i (first (first m))
						  j (second (first m))
						  x (.log js/console "avail move:"  i j)
						  g (assoc g :board (othello-move (:board g) i j (calc-move (:computer g) g i j) (:computer g)))
						  g (assoc g :status (did-win? g)) ]
						(dispatch [:game g]))
					(do
						(.log js/console "not in-progress")
						(dispatch [:game g])))
			))
			(.log js/console " - " x y ": " (calc-move player game x y) ", game:" game )))



(defn new-game [n]
  {:type :othello
   :status :in-progress
   :background-color "#578957"
   :player "B"
   :computer "W"
   :board-size n
   :board
   (let [x1 (dec (quot n 2))
         y1 (dec (quot n 2))
         x2 (quot n 2)
         y2 (quot n 2)]
     (-> (vec (repeat n (vec (repeat n " "))))
         (assoc-in [x1 y1] "W")
         (assoc-in [x2 y1] "B")
         (assoc-in [x1 y2] "B")
         (assoc-in [x2 y2] "W")))})

(defn new-game! []
	(dispatch :game (new-game 8)))