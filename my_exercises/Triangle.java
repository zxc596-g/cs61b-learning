public class Triangle {
    public  static  void printTriangle(int N){
        for(int i=0;i<N;i+=1){
            for(int j=0;j<=i;j+=1){
                System.out.print("*");
            }
            System.out.println();
        }
    }
    public static void main(String[]args){
        printTriangle(10);
    }
}
