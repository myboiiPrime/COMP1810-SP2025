# Data Structures Design Specification
## Online Bookstore Order Processing System

### Document Overview
This document provides a comprehensive design specification for the Abstract Data Types (ADTs) implemented in the Online Bookstore system. Each data structure is designed to solve specific problems in order processing, customer management, and system optimization.

---

## 1. Stack ADT

### Purpose
Implements Last-In-First-Out (LIFO) operations for order history management and undo functionality.

### Design Specifications

#### Data Structure
- **Implementation**: Array-based
- **Capacity**: Fixed maximum of 1000 elements (configurable)
- **Memory Model**: Dynamic allocation with overflow protection

#### Valid Operations

| Operation | Description | Time Complexity | Space Complexity | Preconditions | Postconditions |
|-----------|-------------|-----------------|------------------|---------------|----------------|
| `push(element)` | Adds element to top of stack | O(1) | O(1) | Stack not full | Element added to top, size increased by 1 |
| `pop()` | Removes and returns top element | O(1) | O(1) | Stack not empty | Top element removed, size decreased by 1 |
| `peek()` | Returns top element without removal | O(1) | O(1) | Stack not empty | Top element returned, stack unchanged |
| `isEmpty()` | Checks if stack is empty | O(1) | O(1) | None | Returns boolean status |
| `size()` | Returns number of elements | O(1) | O(1) | None | Returns current size |
| `clear()` | Removes all elements | O(1) | O(1) | None | Stack becomes empty |
| `toArray()` | Returns array representation | O(n) | O(n) | None | Returns copy of stack contents |

#### Invariants
- Stack size ≤ maximum capacity
- Top pointer always points to the last inserted element
- Empty stack returns undefined for pop/peek operations

#### Use Cases in Bookstore System
- Order history tracking
- Undo operations for order modifications
- Transaction rollback functionality

---

## 2. Priority Queue ADT

### Purpose
Implements priority-based ordering for efficient order processing where high-priority orders are processed first.

### Design Specifications

#### Data Structure
- **Implementation**: Binary Max-Heap
- **Priority Model**: Higher numeric values = higher priority
- **Tie-breaking**: Timestamp-based (FIFO for same priority)

#### Valid Operations

| Operation | Description | Time Complexity | Space Complexity | Preconditions | Postconditions |
|-----------|-------------|-----------------|------------------|---------------|----------------|
| `enqueue(data, priority)` | Adds element with priority | O(log n) | O(1) | Valid priority value | Element inserted maintaining heap property |
| `dequeue()` | Removes highest priority element | O(log n) | O(1) | Queue not empty | Highest priority element removed |
| `peek()` | Returns highest priority element | O(1) | O(1) | Queue not empty | Highest priority element returned |
| `isEmpty()` | Checks if queue is empty | O(1) | O(1) | None | Returns boolean status |
| `size()` | Returns number of elements | O(1) | O(1) | None | Returns current size |
| `clear()` | Removes all elements | O(1) | O(1) | None | Queue becomes empty |

#### Heap Properties
- **Max-Heap Property**: Parent priority ≥ child priority
- **Complete Binary Tree**: All levels filled except possibly the last
- **Array Representation**: Parent at index i, children at 2i+1 and 2i+2

#### Priority Levels
- **Urgent**: Priority 4 (VIP customers, express orders)
- **High**: Priority 3 (Premium customers, time-sensitive)
- **Normal**: Priority 2 (Standard orders)
- **Low**: Priority 1 (Bulk orders, non-urgent)

#### Use Cases in Bookstore System
- Order processing queue management
- Customer service request prioritization
- Inventory restocking priority

---

## 3. Circular Queue ADT

### Purpose
Implements fixed-size buffer with wraparound functionality for efficient order processing pipeline.

### Design Specifications

#### Data Structure
- **Implementation**: Fixed-size circular buffer
- **Capacity**: Configurable (default 50)
- **Memory Model**: Pre-allocated array with pointer management

#### Valid Operations

| Operation | Description | Time Complexity | Space Complexity | Preconditions | Postconditions |
|-----------|-------------|-----------------|------------------|---------------|----------------|
| `enqueue(element)` | Adds element to rear | O(1) | O(1) | Queue not full | Element added, rear pointer advanced |
| `dequeue()` | Removes element from front | O(1) | O(1) | Queue not empty | Element removed, front pointer advanced |
| `peek()` | Returns front element | O(1) | O(1) | Queue not empty | Front element returned |
| `peekRear()` | Returns rear element | O(1) | O(1) | Queue not empty | Rear element returned |
| `isEmpty()` | Checks if queue is empty | O(1) | O(1) | None | Returns boolean status |
| `isFull()` | Checks if queue is full | O(1) | O(1) | None | Returns boolean status |
| `size()` | Returns number of elements | O(1) | O(1) | None | Returns current size |
| `remainingCapacity()` | Returns available space | O(1) | O(1) | None | Returns remaining capacity |
| `getUtilization()` | Returns usage percentage | O(1) | O(1) | None | Returns utilization percentage |

#### Circular Buffer Properties
- **Front Pointer**: Points to first element
- **Rear Pointer**: Points to next insertion position
- **Wraparound**: Pointers wrap to 0 when reaching capacity
- **Full Condition**: (rear + 1) % capacity == front
- **Empty Condition**: front == rear && count == 0

#### Use Cases in Bookstore System
- Order processing pipeline
- Request buffering
- Rate limiting and flow control

---

## 4. Deque (Double-ended Queue) ADT

### Purpose
Implements double-ended queue for flexible insertion/deletion at both ends, used for customer management and buffering.

### Design Specifications

#### Data Structure
- **Implementation**: Dynamic array with front/back indices
- **Growth Strategy**: Amortized O(1) operations
- **Memory Model**: Sparse array with index management

#### Valid Operations

| Operation | Description | Time Complexity | Space Complexity | Preconditions | Postconditions |
|-----------|-------------|-----------------|------------------|---------------|----------------|
| `addFront(element)` | Adds element to front | O(1) amortized | O(1) | None | Element added to front |
| `addBack(element)` | Adds element to back | O(1) amortized | O(1) | None | Element added to back |
| `removeFront()` | Removes element from front | O(1) | O(1) | Deque not empty | Front element removed |
| `removeBack()` | Removes element from back | O(1) | O(1) | Deque not empty | Back element removed |
| `peekFront()` | Returns front element | O(1) | O(1) | Deque not empty | Front element returned |
| `peekBack()` | Returns back element | O(1) | O(1) | Deque not empty | Back element returned |
| `isEmpty()` | Checks if deque is empty | O(1) | O(1) | None | Returns boolean status |
| `size()` | Returns number of elements | O(1) | O(1) | None | Returns current size |
| `compact()` | Optimizes memory usage | O(n) | O(n) | None | Memory compacted |

#### Index Management
- **Front Index**: Points to first element
- **Back Index**: Points to next insertion position at back
- **Negative Indices**: Supported for front insertions
- **Auto-reset**: Indices reset when deque becomes empty

#### Use Cases in Bookstore System
- Recent customer tracking
- Flexible order buffering
- Bidirectional data processing

---

## System Integration

### Data Structure Relationships
```
OrderManager
├── PriorityQueue (pending orders)
├── CircularQueue (processing pipeline)
├── Stack (order history)
└── Deque (flexible buffering)

CustomerManager
└── Deque (recent customers)

InventoryManager
└── PriorityQueue (restocking priorities)
```

### Performance Characteristics

| Data Structure | Best Use Case | Primary Advantage | Memory Overhead |
|----------------|---------------|-------------------|------------------|
| Stack | Undo operations | Simple LIFO access | Minimal |
| Priority Queue | Order processing | Priority-based access | Moderate (heap) |
| Circular Queue | Processing pipeline | Fixed memory usage | Low (pre-allocated) |
| Deque | Flexible buffering | Bidirectional access | Variable (sparse) |

### Error Handling

#### Common Error Conditions
- **Overflow**: Stack/Queue exceeds capacity
- **Underflow**: Pop/Dequeue from empty structure
- **Invalid Priority**: Non-numeric priority values
- **Memory Exhaustion**: System memory limits

#### Error Recovery Strategies
- Graceful degradation with fallback operations
- Automatic capacity management where applicable
- Comprehensive logging and monitoring
- Transaction rollback capabilities

---

## Conclusion

This design specification provides a comprehensive framework for the data structures used in the Online Bookstore system. Each ADT is carefully designed to solve specific problems while maintaining optimal performance characteristics and system reliability.

The modular design allows for independent testing, optimization, and maintenance of each component while ensuring seamless integration within the larger system architecture.