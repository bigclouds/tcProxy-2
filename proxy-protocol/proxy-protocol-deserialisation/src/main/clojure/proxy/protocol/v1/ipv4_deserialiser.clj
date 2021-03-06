
(ns proxy.protocol.v1.ipv4-deserialiser
  (import [com.mattunderscore.tcproxy.io.serialisation AbstractByteBufferDeserialiser NotDeserialisableResult NeedsMoreDataResult DeserialisationResult]
          [java.net.Inet4Address])
  (:gen-class
    :name com.mattunderscore.proxy.protocol.v1.PlainTextIPV4Deserialiser
    :extends com.mattunderscore.tcproxy.io.serialisation.AbstractByteBufferDeserialiser))

(defn- is-group-separator [byte]
  (= byte (int \.)))

(defn- is-valid-byte [byte]
  (or (<= (int \0) byte (int \9))
      (is-group-separator byte)))

(defn- create-address-result-from-context [context remaining]
  (let [bytes (into-array Byte/TYPE (map (fn [group] (Integer/parseInt (clojure.string/join (map char group)))) (:groups context)))]
    (DeserialisationResult/create (java.net.Inet4Address/getByAddress bytes) (:processed context) (nil? (first remaining)))))

(defn- has-potential-address [context]
  (and
    (= 3 (count (:groups context)))
    (> (count (:pending context)) 0)))

(defn- is-grouping-invalid [byte context]
  (if (is-group-separator byte)
    (or
      ; Empty group
      (= 0 (count (:pending context)))
      ; Too many groups
      (= 4 (count (:groups context))))
    ; Too many characters in group
    (= 3 (count (:pending context)))))

(defn- append-pending-to-groups [context]
  (-> context
    (update-in [:groups] conj (:pending context))
    (assoc :pending [])))

(declare process-next-byte)

(defn- process-byte [context byte remaining]
  (if (is-valid-byte byte)
    (if (is-grouping-invalid byte context)
      ; Group separator without pending group or too many groups
      (NotDeserialisableResult/create (+ (:processed context) 1))
      ; Accumulate the byte
      (if (is-group-separator byte)
        ; Pack new group and continue
        (process-next-byte remaining (-> context
                                       (update-in [:processed] + 1)
                                       (append-pending-to-groups)))
        ; Add to pending group and continue
        (process-next-byte remaining (-> context
                                       (update-in [:processed] + 1)
                                       (update-in [:pending] conj byte)))))

    ; Has reached a byte that is not a part of the address
    (if (has-potential-address context)
      ; Create the address
      (create-address-result-from-context (append-pending-to-groups context) remaining)
      ; Cannot be an address
      (NotDeserialisableResult/create (+ (:processed context) 1)))))

(defn- process-next-byte [seq context]
  (if-let [byte (first seq)]
    (process-byte context byte (rest seq))
    ; No more data
    (if (has-potential-address context)
      ; Create the address
      (create-address-result-from-context (append-pending-to-groups context) (rest seq))
      ; Ended early
      (NeedsMoreDataResult/INSTANCE))))

(defn- read-ip-from-sequence [seq]
  (process-next-byte seq {:processed 0 :groups [] :pending []}))

(defn -doRead
  [this buffer]

  (let [byte-seq (repeatedly #(if (.hasRemaining buffer) (.get buffer) nil))]
    (read-ip-from-sequence byte-seq)))
