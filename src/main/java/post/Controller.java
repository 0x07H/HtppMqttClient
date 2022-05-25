/*
package post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
public class Controller {
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	@RequestMapping(value = "/", produces = { "application/json;charset=utf-8" }, consumes = {"application/json;charset=utf-8" }, method = RequestMethod.POST)
	public String getRespones(@RequestBody Message msg){
		log.info(String.format("Request: %s", msg));
		log.info(String.format("Request: %s", msg.getMessage()));
		String rqs = msg.getMessage();
		System.out.println(rqs);
		return rqs;
	}
}
*/