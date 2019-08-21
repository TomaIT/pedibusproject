package it.polito.ai.pedibusproject.database.model;

import it.polito.ai.pedibusproject.exceptions.InternalServerErrorException;
import it.polito.ai.pedibusproject.service.interfaces.ChildService;
import it.polito.ai.pedibusproject.service.interfaces.ReservationService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Document(collection = "busrides")
public class BusRide implements Comparable<BusRide> {
    @Id
    private String id; //Creato concatenando idLine,stopBusType,year,month,day
    private String idLine;
    private StopBusType stopBusType;
    private TreeSet<StopBus> stopBuses = new TreeSet<>();

    private Integer year;
    private Integer month;
    private Integer day;

    private Date startTime; //Data:ora in cui inizia la corsa
    private Boolean isEnabled=true; //Stato che indica la cancellazione
    //private Set<String> idReservations = new HashSet<>(); //Prenotazioni per tale corsa

    private Long timestampLastStopBus; //Epoch time
    private String idLastStopBus;

    /*
    Year: 1970-...
    Month: 0-11
    Day: 1-31
     */
    public static Calendar getCalendarOnlyDay(Integer year,Integer month,Integer day){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.AM_PM,Calendar.AM);
        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }

    public BusRide(String idLine, StopBusType stopBusType, TreeSet<StopBus> stopBuses,
                   Integer year,Integer month,Integer day){
        this.idLine=idLine;
        this.stopBuses=stopBuses;
        this.stopBusType=stopBusType;
        this.year=year;
        this.month=month;
        this.day=day;
        this.id=idLine+"."+stopBusType+"."+year.toString()+"."+month.toString()+"."+day.toString();

        Calendar calendar=getCalendarOnlyDay(year,month,day);
        calendar.set(Calendar.MINUTE, Objects.requireNonNull(stopBuses.pollFirst()).getHours().intValue());
        this.startTime=calendar.getTime();
    }

    public void addStopBus(StopBus stopBus){
        this.stopBuses.add(stopBus);
    }

    public byte[] exportExcel(ReservationService reservationService, ChildService childService){
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Children State");
        workbook.getProperties().getCoreProperties().setCreator("pedibus.application");
        if(this.timestampLastStopBus!=null)
        workbook.getProperties().getCoreProperties().setModified(Optional.of(new Date(this.timestampLastStopBus)));
        workbook.getProperties().getCoreProperties().setCreated(Optional.of(this.startTime));

        CellStyle headerStyle = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        style.setWrapText(true);

        Cell headerCell;
        Cell cell;Child child;Row row;StopBus stopBus;
        int r=2;

        Row header = sheet.createRow(0);

        headerCell = header.createCell(0);
        headerCell.setCellValue("Child");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Stop_Bus_In");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Date_Get_In");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Escort");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Stop_Bus_Out");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Date_Get_Out");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue("Escort");
        headerCell.setCellStyle(headerStyle);


        //TODO
        Set<Reservation> reservations=reservationService.findAllByIdBusRide(id);

        r=2;
        for(Reservation reservation:reservations){
            row = sheet.createRow(r);

            child=childService.findById(reservation.getIdChild());

            cell = row.createCell(0);
            cell.setCellValue(child.getFirstname()+" "+child.getSurname());
            cell.setCellStyle(style);


            if(reservation.getGetIn()!=null) {
                cell = row.createCell(1);
                stopBus = stopBuses.stream()
                        .filter(x -> x.getId().equals(reservation.getGetIn().getIdStopBus())).findFirst()
                        .orElseThrow(() -> new InternalServerErrorException("Id Stop Bus GetIn reservation not present in busRide"));
                cell.setCellValue(stopBus.getName());
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue((new Date(reservation.getGetIn().getEpochTime())).toString());
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue(reservation.getGetIn().getIdUser());
                cell.setCellStyle(style);
            }else{
                cell = row.createCell(1);
                cell.setCellValue("-");
                cell.setCellStyle(style);

                cell = row.createCell(2);
                cell.setCellValue("-");
                cell.setCellStyle(style);

                cell = row.createCell(3);
                cell.setCellValue("-");
                cell.setCellStyle(style);
            }


            if(reservation.getGetOut()!=null) {
                cell = row.createCell(4);
                stopBus = stopBuses.stream()
                        .filter(x -> x.getId().equals(reservation.getGetOut().getIdStopBus())).findFirst()
                        .orElseThrow(() -> new InternalServerErrorException("Id Stop Bus GetIn reservation not present in busRide"));
                cell.setCellValue(stopBus.getName());
                cell.setCellStyle(style);

                cell = row.createCell(5);
                cell.setCellValue((new Date(reservation.getGetOut().getEpochTime())).toString());
                cell.setCellStyle(style);

                cell = row.createCell(6);
                cell.setCellValue(reservation.getGetOut().getIdUser());
                cell.setCellStyle(style);
            }else{
                cell = row.createCell(4);
                cell.setCellValue("-");
                cell.setCellStyle(style);

                cell = row.createCell(5);
                cell.setCellValue("-");
                cell.setCellStyle(style);

                cell = row.createCell(6);
                cell.setCellValue("-");
                cell.setCellStyle(style);
            }

            r++;
        }

        sheet.autoSizeColumn(0,true);
        sheet.autoSizeColumn(1,true);
        sheet.autoSizeColumn(2,true);
        sheet.autoSizeColumn(3,true);
        sheet.autoSizeColumn(4,true);
        sheet.autoSizeColumn(5,true);
        sheet.autoSizeColumn(6,true);


        ByteArrayOutputStream out=new ByteArrayOutputStream();
        try {
            workbook.write(out);
            workbook.close();
        }catch (Exception e){e.printStackTrace();return null;}
        return out.toByteArray();
        //return null;
    }

    @Override
    public int compareTo(BusRide o) {
        if(this.idLine.equals(o.idLine)){
            if(this.startTime.equals(o.startTime)){
                return this.id.compareTo(o.id);
            }
            return this.startTime.compareTo(o.startTime);
        }
        return this.idLine.compareTo(o.idLine);
    }
}
