package cc.tweaked.waluaigi;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BasicEnvironment implements ExecutionEnvironment {
    private InvokeObject[] objects = new InvokeObject[16];
    private int nextFree = 0;

    public int allocate(InvokeObject object) {
        Objects.requireNonNull(object, "object cannot be null");

        if (nextFree >= objects.length) objects = Arrays.copyOf(objects, objects.length << 1);

        int index = nextFree++;
        objects[index] = object;
        return index;
    }

    public boolean isFree(int object) {
        return objects[object] == null;
    }

    @Override
    public void free(int object) {
        assertInhabited(object);
        objects[object] = null;
    }

    @Override
    public int invoke(int luaState, int object, int method) {
        assertInhabited(object);
        return objects[object].invoke(luaState, method);
    }

    @Override
    public TimeoutMode getTimeout() {
        return TimeoutMode.OK;
    }

    private void assertInhabited(int object) {
        assertNotNull(objects[object], () -> {
            StringBuilder free = new StringBuilder();
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] != null) free.append(i).append(", ");
            }

            return "No object at slot " + object + "(free slots are " + free + ")";
        });
    }

    @FunctionalInterface
    public interface InvokeObject {
        int invoke(int luaState, int method);
    }
}
