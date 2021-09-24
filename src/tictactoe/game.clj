(ns tictactoe.game)

(def players ["X" "O"])

(def available-moves (atom [1 2 3 4 5 6 7 8 9]))

(defn- board []
  (vec (repeat 3 (vec (repeat 3 nil)))))

(defn- fill-with-blanks [blanks]
  (map #(if (nil? %) "-" %) blanks))

(defn- print-board [board]
  (let [rows (map fill-with-blanks board)]
    (println)
    (doall (map #(apply println %) rows))
    (println)))

(defn- int-to-coords [matrix n]
  ((juxt quot rem) n (count (first matrix))))

(defn- put-xo [board xo place]
  (let [position (int-to-coords board (dec place))]
    (if (and (< (dec place) 9)
             (= nil (get-in board position)))
      (assoc-in board position xo) board)))

(defn- win [game xo]
  (filter #(apply = xo %) game))

(defn- horizontal-win [board player]
  (let [victor (win board player)]
    (some? (seq victor))))

(defn- vertical-win [board player]
  (let [rotated-board (apply map vector board)]
    (horizontal-win rotated-board player)))

(defn- diagonal-win [board player]
  (let [diagonal-1 (map #(get-in board %) (apply map vector [(range 3) (range 3)]))
        diagonal-2 (map #(get-in board %) (apply map vector [(reverse (range 3)) (range 3)]))]
    (some? (seq (win [diagonal-1 diagonal-2] player)))))

(defn- win? [board player]
  (or (diagonal-win board player)
      (vertical-win board player)
      (horizontal-win board player)))

(defn- full? [board]
  (every? #(not= nil %) (flatten board)))

(defn- outcome [board players]
  (if-let [winner (seq (filter (partial win? board) players))]
    (first winner)
    (if (full? board)
      :draw nil)))

(defn round []
  (let [turn (cycle players)
        user (rand-nth players)]
    (println)
    (println "You play as" user)
    (println)
    (loop [board (board) turn turn]
      (if-let [win-or-draw (outcome board players)]
        (do (print-board board)
            (if (= :draw win-or-draw)
              (println "draw")
              (println win-or-draw "wins")))
        (let [player (first turn)]
          (if (= player user)
            (do (print-board board)
                (println "your move > ")
                (let [next-move (read-string (read-line))
                      new-board (put-xo board player next-move)]
                  (if (not= new-board board)
                    (do (swap! available-moves (fn [r] (remove #{next-move} r)))
                        (recur new-board (rest turn)))
                    (do (println "illegal move")
                        (recur board turn)))))
            (let [comp-move (rand-nth @available-moves)
                  new-board (put-xo board player comp-move)]
              (do (swap! available-moves (fn [r] (remove #{comp-move} r)))
                  (recur new-board (rest turn))))))))))