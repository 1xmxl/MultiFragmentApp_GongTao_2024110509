# 保留 Serializable 类，避免混淆后 serialVersionUID 不匹配导致历史记录反序列化失败
-keep class com.example.multifragmentapp.model.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
