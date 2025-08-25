package com.bookstore.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


@RestController
@RequestMapping("/data-structures")
public class DataStructuresController {

    // In-memory storage for demonstration
    private Stack<String> stack = new Stack<>();
    private Queue<String> queue = new ConcurrentLinkedQueue<>();
    private Queue<String> circularQueue = new LinkedList<>();
    private Deque<String> deque = new ArrayDeque<>();
    private PriorityQueue<String> priorityQueue = new PriorityQueue<>();
    private Map<String, String> hashMap = new HashMap<>();
    
    private int circularQueueCapacity = 5;

    // Stack Operations
    @PostMapping("/stack/push")
    public ResponseEntity<Map<String, Object>> stackPush(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            stack.push(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value pushed to stack",
                "stack", new ArrayList<>(stack),
                "size", stack.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error pushing to stack: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/stack/pop")
    public ResponseEntity<Map<String, Object>> stackPop() {
        try {
            if (stack.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Stack is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String poppedValue = stack.pop();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value popped from stack",
                "poppedValue", poppedValue,
                "stack", new ArrayList<>(stack),
                "size", stack.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error popping from stack: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/stack/peek")
    public ResponseEntity<Map<String, Object>> stackPeek() {
        try {
            if (stack.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Stack is empty",
                    "stack", new ArrayList<>(),
                    "size", 0
                ));
            }
            
            long startTime = System.nanoTime();
            String topValue = stack.peek();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "topValue", topValue,
                "stack", new ArrayList<>(stack),
                "size", stack.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error peeking stack: " + e.getMessage()
            ));
        }
    }

    // Queue Operations
    @PostMapping("/queue/enqueue")
    public ResponseEntity<Map<String, Object>> queueEnqueue(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            queue.offer(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value enqueued",
                "queue", new ArrayList<>(queue),
                "size", queue.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error enqueuing: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/queue/dequeue")
    public ResponseEntity<Map<String, Object>> queueDequeue() {
        try {
            if (queue.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Queue is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String dequeuedValue = queue.poll();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value dequeued",
                "dequeuedValue", dequeuedValue,
                "queue", new ArrayList<>(queue),
                "size", queue.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error dequeuing: " + e.getMessage()
            ));
        }
    }

    // Circular Queue Operations
    @PostMapping("/circular-queue/enqueue")
    public ResponseEntity<Map<String, Object>> circularQueueEnqueue(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            if (circularQueue.size() >= circularQueueCapacity) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Circular queue is full (capacity: " + circularQueueCapacity + ")"
                ));
            }
            
            long startTime = System.nanoTime();
            circularQueue.offer(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value enqueued to circular queue",
                "queue", new ArrayList<>(circularQueue),
                "size", circularQueue.size(),
                "capacity", circularQueueCapacity,
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error enqueuing to circular queue: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/circular-queue/dequeue")
    public ResponseEntity<Map<String, Object>> circularQueueDequeue() {
        try {
            if (circularQueue.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Circular queue is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String dequeuedValue = circularQueue.poll();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value dequeued from circular queue",
                "dequeuedValue", dequeuedValue,
                "queue", new ArrayList<>(circularQueue),
                "size", circularQueue.size(),
                "capacity", circularQueueCapacity,
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error dequeuing from circular queue: " + e.getMessage()
            ));
        }
    }

    // Deque Operations
    @PostMapping("/deque/push-front")
    public ResponseEntity<Map<String, Object>> dequePushFront(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            deque.offerFirst(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value pushed to front of deque",
                "deque", new ArrayList<>(deque),
                "size", deque.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error pushing to front of deque: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/deque/push-rear")
    public ResponseEntity<Map<String, Object>> dequePushRear(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            deque.offerLast(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value pushed to rear of deque",
                "deque", new ArrayList<>(deque),
                "size", deque.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error pushing to rear of deque: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/deque/pop-front")
    public ResponseEntity<Map<String, Object>> dequePopFront() {
        try {
            if (deque.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Deque is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String poppedValue = deque.pollFirst();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value popped from front of deque",
                "poppedValue", poppedValue,
                "deque", new ArrayList<>(deque),
                "size", deque.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error popping from front of deque: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/deque/pop-rear")
    public ResponseEntity<Map<String, Object>> dequePopRear() {
        try {
            if (deque.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Deque is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String poppedValue = deque.pollLast();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value popped from rear of deque",
                "poppedValue", poppedValue,
                "deque", new ArrayList<>(deque),
                "size", deque.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error popping from rear of deque: " + e.getMessage()
            ));
        }
    }

    // Priority Queue Operations
    @PostMapping("/priority-queue/add")
    public ResponseEntity<Map<String, Object>> priorityQueueAdd(@RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            priorityQueue.offer(value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value added to priority queue",
                "queue", new ArrayList<>(priorityQueue),
                "size", priorityQueue.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error adding to priority queue: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/priority-queue/poll")
    public ResponseEntity<Map<String, Object>> priorityQueuePoll() {
        try {
            if (priorityQueue.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Priority queue is empty"
                ));
            }
            
            long startTime = System.nanoTime();
            String polledValue = priorityQueue.poll();
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Value polled from priority queue",
                "polledValue", polledValue,
                "queue", new ArrayList<>(priorityQueue),
                "size", priorityQueue.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error polling from priority queue: " + e.getMessage()
            ));
        }
    }

    // Hash Search Operations
    @PostMapping("/hash-search/put")
    public ResponseEntity<Map<String, Object>> hashSearchPut(@RequestBody Map<String, String> request) {
        try {
            String key = request.get("key");
            String value = request.get("value");
            
            if (key == null || key.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Key is required"
                ));
            }
            
            if (value == null || value.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Value is required"
                ));
            }
            
            long startTime = System.nanoTime();
            hashMap.put(key.trim(), value.trim());
            long endTime = System.nanoTime();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Key-value pair added to hash map",
                "hashMap", hashMap,
                "size", hashMap.size(),
                "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error adding to hash map: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/hash-search/get")
    public ResponseEntity<Map<String, Object>> hashSearchGet(@RequestParam String key) {
        try {
            if (key == null || key.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Key is required"
                ));
            }
            
            long startTime = System.nanoTime();
            String value = hashMap.get(key.trim());
            long endTime = System.nanoTime();
            
            if (value != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Key found",
                    "key", key.trim(),
                    "value", value,
                    "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Key not found",
                    "key", key.trim(),
                    "executionTime", (endTime - startTime) / 1000.0 + " microseconds"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error searching hash map: " + e.getMessage()
            ));
        }
    }

    // Load demo data
    @PostMapping("/load-demo")
    public ResponseEntity<Map<String, Object>> loadDemoData() {
        try {
            // Clear existing data
            stack.clear();
            queue.clear();
            circularQueue.clear();
            deque.clear();
            priorityQueue.clear();
            hashMap.clear();
            
            // Load demo data
            stack.push("Book1");
            stack.push("Book2");
            stack.push("Book3");
            
            queue.offer("Order1");
            queue.offer("Order2");
            queue.offer("Order3");
            
            circularQueue.offer("Task1");
            circularQueue.offer("Task2");
            
            deque.offerFirst("Front1");
            deque.offerLast("Rear1");
            
            priorityQueue.offer("High");
            priorityQueue.offer("Medium");
            priorityQueue.offer("Low");
            
            hashMap.put("ISBN001", "Java Programming");
            hashMap.put("ISBN002", "Data Structures");
            hashMap.put("ISBN003", "Algorithms");
            
            Map<String, Object> data = new HashMap<>();
            data.put("stack", new ArrayList<>(stack));
            data.put("queue", new ArrayList<>(queue));
            data.put("circularQueue", new ArrayList<>(circularQueue));
            data.put("deque", new ArrayList<>(deque));
            data.put("priorityQueue", new ArrayList<>(priorityQueue));
            data.put("hashMap", hashMap);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Demo data loaded successfully");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error loading demo data: " + e.getMessage()
            ));
        }
    }

    // Get current state of all data structures
    @GetMapping("/state")
    public ResponseEntity<Map<String, Object>> getCurrentState() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            Map<String, Object> stackData = new HashMap<>();
            stackData.put("data", new ArrayList<>(stack));
            stackData.put("size", stack.size());
            data.put("stack", stackData);
            
            Map<String, Object> queueData = new HashMap<>();
            queueData.put("data", new ArrayList<>(queue));
            queueData.put("size", queue.size());
            data.put("queue", queueData);
            
            Map<String, Object> circularQueueData = new HashMap<>();
            circularQueueData.put("data", new ArrayList<>(circularQueue));
            circularQueueData.put("size", circularQueue.size());
            circularQueueData.put("capacity", circularQueueCapacity);
            data.put("circularQueue", circularQueueData);
            
            Map<String, Object> dequeData = new HashMap<>();
            dequeData.put("data", new ArrayList<>(deque));
            dequeData.put("size", deque.size());
            data.put("deque", dequeData);
            
            Map<String, Object> priorityQueueData = new HashMap<>();
            priorityQueueData.put("data", new ArrayList<>(priorityQueue));
            priorityQueueData.put("size", priorityQueue.size());
            data.put("priorityQueue", priorityQueueData);
            
            Map<String, Object> hashMapData = new HashMap<>();
            hashMapData.put("data", hashMap);
            hashMapData.put("size", hashMap.size());
            data.put("hashMap", hashMapData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error getting current state: " + e.getMessage()
            ));
        }
    }
}