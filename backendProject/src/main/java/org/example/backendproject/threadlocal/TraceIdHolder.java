package org.example.backendproject.threadlocal;

//이 클래스는 스레드마다 고유한 요청 ID를 저장하고 꺼내는 역할ㅇ르 함
public class TraceIdHolder {


    //traceId
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    // threadlocal에 값을 저장하고 꺼낼  set 메서드와 get 메서드
    public static void set(String traceId) {
        threadLocal.set(traceId);
    }

    public static String get() {
        return threadLocal.get();
    }

    // 하나의 요청이 끝났을 때 threadlocal에 저장된 값을 지우기 위한 clear 메소드
    public static void clear() {
        threadLocal.remove();
    }
}
