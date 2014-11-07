(ns stream.mist.hundred-doors
  "Hundred Door Puzzle


  Problem: We have 100 doors in a row that are all initially closed. We have to
  make 100 passes by the doors. The first time through, you visit every
  door and toggle the door (if the door is closed, you open it; if it is
  open, you close it).

  The second time you only visit every 2nd door (door #2, #4, #6, ...).
  The third time, every 3rd door (door #3, #6, #9, ...), etc, until you
  only visit the 100th door.

  Question: What state are the doors in after the last pass?
  Which are open, which are closed?

  Various other implementations
    * http://rosettacode.org/wiki/100_doors
    * http://www.quora.com/How-do-I-write-an-algorithm-for-the-100-doors-problem")


(defn pass-through-doors
  "Logic works similar to the below snippet

    (reduce (fn [v a] (+ v a))
            0
            [1 2 3 4])

  reduce applies the fn for each value (a) in the vec
  with initial of 0 (v)"
  [doors idxs]
  (reduce (fn [door id] (assoc door id (not (get door id))))
          doors
          idxs))

(defn open-doors
  "Initialize the doors with false to represent the closed state
   Create a list of all possible combination of passes
       #2, #4, #6, ..., #3, #6, #9, ...
   Index the result vector and pickout the open doors (= true)"
  [no]
  (let [doors (into [] (repeat (+ 1 no) false))
        pass-ids (for [id (range 1 (+ 1 no)) n (range 1 (+ 1 no)) :when (= (mod n id) 0)] n)
        ptd (pass-through-doors doors pass-ids)]
    (for [[idx elt] (map vector (range) ptd) :when elt]
      idx)))

;Opens doors are (1 , 4 , 9 , 16 , 25 , 36 , 49 , 64 , 81 , 100)
(println "Opens doors are" (interpose "," (open-doors 100)))
