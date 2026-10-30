/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ProjectSwing;

import java.util.Arrays;
// DSA 
// The pending requests wait here in the order they were sent (first in, first out). FIFO
// It stores request numbers only (REQ-3 is stored as 3).
public class RequestQueue {

    static int[] queue = new int[10];
    static int front = 0;
    static int rear = -1;

    public static boolean isEmpty(){
        return rear < front;
    }

    public static boolean isFull(){
        return rear == queue.length - 1;
    }

    public static int size(){
        return rear - front + 1;
    }

    // empties the queue, used before it is rebuilt from the file
    public static void clear(){
        front = 0;
        rear = -1;
    }

    public static void enqueue(int data){
        if (isFull()) {
            queue = Arrays.copyOf(queue, queue.length * 2);   
        }
        rear++;
        queue[rear] = data;
    }

    
    public static int peek(){
        if (isEmpty()) {
            return -1;
        }
        return queue[front];
    }

    // removes the request that is next in line
    public static int dequeue(){
        if (isEmpty()) {
            return -1;
        }
        int data = queue[front];
        for (int i = front; i < rear; i++) {   // shift elements to the left
            queue[i] = queue[i + 1];
        }
        rear--;
        return data;
    }
}
