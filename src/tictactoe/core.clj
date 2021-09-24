(ns tictactoe.core
  (:require [tictactoe.game :as game]))

(defn intro []
  (println "Clojure TIC TAC TOE")
  (println " 1 2 3  To play the game select the field")
  (println " 4 5 6  by entering a digit from 1 to 9")
  (println " 7 8 9  and confirm your move with enter"))

(defn -main []
    (intro)
    (game/round))