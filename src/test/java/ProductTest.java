import java.util.Iterator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.neo4j.conversion.Result;

import com.sp.socialcommerce.neo4j.domain.Product;
import com.sp.socialcommerce.neo4j.service.ProductService;


public class ProductTest {
   public static void main(String[] args) {
      ApplicationContext context = new ClassPathXmlApplicationContext("product.xml");		
      ProductService service = (ProductService) context.getBean("ProductService");

      // Please uncomment one of the operation section 
      // and comment remaining section to test only one operation at a time
      // Here I've uncommented CREATE operation and 
      // commented other operations: FIND ONE, FIND ALL, DELETE
      // CREATE Operation
      Product product = createProduct();
      createProduct(service,product);		
      System.out.println("Product created successfully.");

      // FIND ONE 
      /*
      Product product = getOneProductById(service,67515L);		
      System.out.println(product);
      */

      // FIND ALL
      /*
      getAllProducts(service);		
      */

      // DELETE 
      /*
      Product product = createPofile();
      deleteProduct(service,product);		
      System.out.println("Product deleted successfully.");		
      */
   }
   
   private static Product createProduct(ProductService service, Product product){
      return service.create(product);
   }	
   
   private static void deleteProduct(ProductService service,Product product){
      service.delete(product);
   }	
   
   private static Product getOneProductById(ProductService service,Long id){
      return service.findById(id);
   }	
   
   private static void getAllProducts(ProductService service){
      Result<Product> result = service.findAll();			
      Iterator<Product> iterator = result.iterator();
      
      while(iterator.hasNext()){
         System.out.println(iterator.next());
      }
   }
   
   private static Product createProduct(){
      Product product = new Product();		
      product.setName("Product-2");
      product.setDescription("Hyderabadaaaaa");
      product.setName("Best_name_eva");
      return product;
   }
}