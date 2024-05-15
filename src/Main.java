public class Main {
    public static void main(String[] args) {

        HashMap<String, String> hashMap = new HashMap<>(4);
        hashMap.put("1234", "value");
        hashMap.put("0234", "val");
        String result1 = hashMap.put("2345", "v");
        if (result1 != null) {
            hashMap.put("2345", result1);
        }
        String res2 = hashMap.put("1234", "vvv");
        if (res2 != null) {
            hashMap.put("1234", res2);
        }
        while (hashMap.iterator().hasNext()) {
            System.out.println(hashMap.iterator().next());
        }
//        System.out.println(hashMap);
//        System.out.println(result1);
//        System.out.println(res2);




    }
}