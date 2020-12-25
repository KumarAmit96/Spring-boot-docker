package net.guides.springboot2.springboot2jpacrudexample.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.guides.springboot2.springboot2jpacrudexample.exception.ResourceNotFoundException;
import net.guides.springboot2.springboot2jpacrudexample.model.Bot;
import net.guides.springboot2.springboot2jpacrudexample.model.Employee;
import net.guides.springboot2.springboot2jpacrudexample.repository.EmployeeRepository;

@RestController
@RequestMapping("/user")
public class BotController {
	
	@Autowired
	private BotRepository botRepository;

	@PostMapping("/{userId}/bots")
	@ResponseBody
	public ResponseEntity<Bot> getBotCreate(@PathVariable(value = "userId") int userId,
			@RequestParam(defaultValue = null) String containerId,
			@RequestParam(defaultValue = "STARTING") String status))throws ResourceNotFoundException {
		
			DockerClient dockerClient = DockerClientBuilder.getInstance().build();
			CreateContainerResponse container
			  = dockerClient.createContainerCmd("mongo:3.6")
			    .withCmd("--bind_ip_all")
			    .withName("mongo")
			    .withHostName("baeldung")
			    .withEnv("MONGO_LATEST_VERSION=3.6")
			    .withPortBindings(PortBinding.parse("9999:27017"))
			    .withBinds(Bind.parse("/Users/baeldung/mongo/data/db:/data/db")).exec();
			Bot bot = botRepository.save(new Bot(container.getId, userId, "RUNNING"));
			return new ResponseEntity<>(bot, HttpStatus.CREATED);

	}

	@GetMapping("/{userId}/bots/{botId}/list")
	public ResponseEntity<List<Bot>> getListById(@PathVariable(value = "userId") int userId,
			@PathVariable(value = "botId") String botId)
			throws ResourceNotFoundException {
		
		
		Bot bot = employeeRepository.findById(userId);
		
		if(bot == null)
		{
			new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		else if(bot.getContainerId.equals(botId))
		{
			List<Bot> botList = new ArrayList<>();
			DockerClient dockerClient = DockerClientBuilder.getInstance().build();
			List<Container> containers = dockerClient.listContainersCmd().exec();
			for(Container container : containers)
			{
				botList.add(new Bot(container.getId(), userId, container.status()))
			}
			return new ResponseEntity<>(botList, HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	
}
