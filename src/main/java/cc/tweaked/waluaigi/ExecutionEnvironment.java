package cc.tweaked.waluaigi;

public interface ExecutionEnvironment {
    void free(int object);

    int invoke(int luaState, int object, int method);

    TimeoutMode getTimeout();
}
