% First: change bars to be grouped alternatively, in storage suage and
% satisfaction rates; Then, provide labels on the alternations/groups
% (whatever works better)

clear

SS1 = dlmread('one-comp/officeIoT/RSMR1', ' ', 0, 1);
HS1 = dlmread('one-comp/homeIoT/RSMR1', ' ', 0, 1);
BS1 = dlmread('one-comp/buses/RSMR1', ' ', 0, 1);
PS1 = dlmread('one-comp/non-proc_proc/RSMR1', ' ', 0, 1);


SS2 = dlmread('one-comp/officeIoT/RSMR2', ' ', 0, 1);
HS2 = dlmread('one-comp/homeIoT/RSMR2', ' ', 0, 1);
BS2 = dlmread('one-comp/buses/RSMR2', ' ', 0, 1);
PS2 = dlmread('one-comp/non-proc_proc/RSMR2', ' ', 0, 1);


SS3 = dlmread('one-comp/officeIoT/RSMR3', ' ', 0, 1);
HS3 = dlmread('one-comp/homeIoT/RSMR3', ' ', 0, 1);
BS3 = dlmread('one-comp/buses/RSMR3', ' ', 0, 1);
PS3 = dlmread('one-comp/non-proc_proc/RSMR3', ' ', 0, 1);


SS4 = dlmread('one-comp/officeIoT/RSMR4', ' ', 0, 1);
HS4 = dlmread('one-comp/homeIoT/RSMR4', ' ', 0, 1);
BS4 = dlmread('one-comp/buses/RSMR4', ' ', 0, 1);
PS4 = dlmread('one-comp/non-proc_proc/RSMR4', ' ', 0, 1);


SS5 = dlmread('one-comp/officeIoT/RSMR5', ' ', 0, 1);
HS5 = dlmread('one-comp/homeIoT/RSMR5', ' ', 0, 1);
BS5 = dlmread('one-comp/buses/RSMR5', ' ', 0, 1);
PS5 = dlmread('one-comp/non-proc_proc/RSMR5', ' ', 0, 1);


SS6 = dlmread('one-comp/officeIoT/RSMR6', ' ', 0, 1);
HS6 = dlmread('one-comp/homeIoT/RSMR6', ' ', 0, 1);
BS6 = dlmread('one-comp/buses/RSMR6', ' ', 0, 1);
PS6 = dlmread('one-comp/non-proc_proc/RSMR6', ' ', 0, 1);





OCB1 = dlmread('one-comp/officeIoT/RCSBWR1', ' ', 0, 1);
HCB1 = dlmread('one-comp/homeIoT/RCSBWR1', ' ', 0, 1);
BCB1 = dlmread('one-comp/buses/RCSBWR1', ' ', 0, 1);
PCB1 = dlmread('one-comp/non-proc_proc/RCSBWR1', ' ', 0, 1);


OCB2 = dlmread('one-comp/officeIoT/RCSBWR2', ' ', 0, 1);
HCB2 = dlmread('one-comp/homeIoT/RCSBWR2', ' ', 0, 1);
BCB2 = dlmread('one-comp/buses/RCSBWR2', ' ', 0, 1);
PCB2 = dlmread('one-comp/non-proc_proc/RCSBWR2', ' ', 0, 1);


OCB3 = dlmread('one-comp/officeIoT/RCSBWR3', ' ', 0, 1);
HCB3 = dlmread('one-comp/homeIoT/RCSBWR3', ' ', 0, 1);
BCB3 = dlmread('one-comp/buses/RCSBWR3', ' ', 0, 1);
PCB3 = dlmread('one-comp/non-proc_proc/RCSBWR3', ' ', 0, 1);


OCB4 = dlmread('one-comp/officeIoT/RCSBWR4', ' ', 0, 1);
HCB4 = dlmread('one-comp/homeIoT/RCSBWR4', ' ', 0, 1);
BCB4 = dlmread('one-comp/buses/RCSBWR4', ' ', 0, 1);
PCB4 = dlmread('one-comp/non-proc_proc/RCSBWR4', ' ', 0, 1);


OCB5 = dlmread('one-comp/officeIoT/RCSBWR5', ' ', 0, 1);
HCB5 = dlmread('one-comp/homeIoT/RCSBWR5', ' ', 0, 1);
BCB5 = dlmread('one-comp/buses/RCSBWR5', ' ', 0, 1);
PCB5 = dlmread('one-comp/non-proc_proc/RCSBWR5', ' ', 0, 1);


OCB6 = dlmread('one-comp/officeIoT/RCSBWR6', ' ', 0, 1);
HCB6 = dlmread('one-comp/homeIoT/RCSBWR6', ' ', 0, 1);
BCB6 = dlmread('one-comp/buses/RCSBWR6', ' ', 0, 1);
PCB6 = dlmread('one-comp/non-proc_proc/RCSBWR6', ' ', 0, 1);

OSB1 = dlmread('one-comp/officeIoT/RSBWR1', ' ', 0, 1);
HSB1 = dlmread('one-comp/homeIoT/RSBWR1', ' ', 0, 1);
BSB1 = dlmread('one-comp/buses/RSBWR1', ' ', 0, 1);
PSB1 = dlmread('one-comp/non-proc_proc/RSBWR1', ' ', 0, 1);


OSB2 = dlmread('one-comp/officeIoT/RSBWR2', ' ', 0, 1);
HSB2 = dlmread('one-comp/homeIoT/RSBWR2', ' ', 0, 1);
BSB2 = dlmread('one-comp/buses/RSBWR2', ' ', 0, 1);
PSB2 = dlmread('one-comp/non-proc_proc/RSBWR2', ' ', 0, 1);


OSB3 = dlmread('one-comp/officeIoT/RSBWR3', ' ', 0, 1);
HSB3 = dlmread('one-comp/homeIoT/RSBWR3', ' ', 0, 1);
BSB3 = dlmread('one-comp/buses/RSBWR3', ' ', 0, 1);
PSB3 = dlmread('one-comp/non-proc_proc/RSBWR3', ' ', 0, 1);


OSB4 = dlmread('one-comp/officeIoT/RSBWR4', ' ', 0, 1);
HSB4 = dlmread('one-comp/homeIoT/RSBWR4', ' ', 0, 1);
BSB4 = dlmread('one-comp/buses/RSBWR4', ' ', 0, 1);
PSB4 = dlmread('one-comp/non-proc_proc/RSBWR4', ' ', 0, 1);


OSB5 = dlmread('one-comp/officeIoT/RSBWR5', ' ', 0, 1);
HSB5 = dlmread('one-comp/homeIoT/RSBWR5', ' ', 0, 1);
BSB5 = dlmread('one-comp/buses/RSBWR5', ' ', 0, 1);
PSB5 = dlmread('one-comp/non-proc_proc/RSBWR5', ' ', 0, 1);


OSB6 = dlmread('one-comp/officeIoT/RSBWR6', ' ', 0, 1);
HSB6 = dlmread('one-comp/homeIoT/RSBWR6', ' ', 0, 1);
BSB6 = dlmread('one-comp/buses/RSBWR6', ' ', 0, 1);
PSB6 = dlmread('one-comp/non-proc_proc/RSBWR6', ' ', 0, 1);



% deleted size for deleted BW and deleted service perc.
OD1 = dlmread('one-cloud/officeIoT/RSBWR1', ' ', 0, 1);
HD1 = dlmread('one-cloud/homeIoT/RSBWR1', ' ', 0, 1);
BD1 = dlmread('one-cloud/buses/RSBWR1', ' ', 0, 1);
PD1 = dlmread('one-cloud/non-proc_proc/RSBWR1', ' ', 0, 1);


OD2 = dlmread('one-cloud/officeIoT/RSBWR2', ' ', 0, 1);
HD2 = dlmread('one-cloud/homeIoT/RSBWR2', ' ', 0, 1);
BD2 = dlmread('one-cloud/buses/RSBWR2', ' ', 0, 1);
PD2 = dlmread('one-cloud/non-proc_proc/RSBWR2', ' ', 0, 1);


OD3 = dlmread('one-cloud/officeIoT/RSBWR3', ' ', 0, 1);
HD3 = dlmread('one-cloud/homeIoT/RSBWR3', ' ', 0, 1);
BD3 = dlmread('one-cloud/buses/RSBWR3', ' ', 0, 1);
PD3 = dlmread('one-cloud/non-proc_proc/RSBWR3', ' ', 0, 1);


OD4 = dlmread('one-cloud/officeIoT/RSBWR4', ' ', 0, 1);
HD4 = dlmread('one-cloud/homeIoT/RSBWR4', ' ', 0, 1);
BD4 = dlmread('one-cloud/buses/RSBWR4', ' ', 0, 1);
PD4 = dlmread('one-cloud/non-proc_proc/RSBWR4', ' ', 0, 1);


OD5 = dlmread('one-cloud/officeIoT/RSBWR5', ' ', 0, 1);
HD5 = dlmread('one-cloud/homeIoT/RSBWR5', ' ', 0, 1);
BD5 = dlmread('one-cloud/buses/RSBWR5', ' ', 0, 1);
PD5 = dlmread('one-cloud/non-proc_proc/RSBWR5', ' ', 0, 1);


OD6 = dlmread('one-cloud/officeIoT/RSBWR6', ' ', 0, 1);
HD6 = dlmread('one-cloud/homeIoT/RSBWR6', ' ', 0, 1);
BD6 = dlmread('one-cloud/buses/RSBWR6', ' ', 0, 1);
PD6 = dlmread('one-cloud/non-proc_proc/RSBWR6', ' ', 0, 1);




%Input BW for deleted service perc.
OIB1 = dlmread('one-cloud/officeIoT/RISR1', ' ', 0, 1);
HIB1 = dlmread('one-cloud/homeIoT/RISR1', ' ', 0, 1);
BIB1 = dlmread('one-cloud/buses/RISR1', ' ', 0, 1);
PIB1 = dlmread('one-cloud/non-proc_proc/RISR1', ' ', 0, 1);


OIB2 = dlmread('one-cloud/officeIoT/RISR2', ' ', 0, 1);
HIB2 = dlmread('one-cloud/homeIoT/RISR2', ' ', 0, 1);
BIB2 = dlmread('one-cloud/buses/RISR2', ' ', 0, 1);
PIB2 = dlmread('one-cloud/non-proc_proc/RISR2', ' ', 0, 1);


OIB3 = dlmread('one-cloud/officeIoT/RISR3', ' ', 0, 1);
HIB3 = dlmread('one-cloud/homeIoT/RISR3', ' ', 0, 1);
BIB3 = dlmread('one-cloud/buses/RISR3', ' ', 0, 1);
PIB3 = dlmread('one-cloud/non-proc_proc/RISR3', ' ', 0, 1);


OIB4 = dlmread('one-cloud/officeIoT/RISR4', ' ', 0, 1);
HIB4 = dlmread('one-cloud/homeIoT/RISR4', ' ', 0, 1);
BIB4 = dlmread('one-cloud/buses/RISR4', ' ', 0, 1);
PIB4 = dlmread('one-cloud/non-proc_proc/RISR4', ' ', 0, 1);


OIB5 = dlmread('one-cloud/officeIoT/RISR5', ' ', 0, 1);
HIB5 = dlmread('one-cloud/homeIoT/RISR5', ' ', 0, 1);
BIB5 = dlmread('one-cloud/buses/RISR5', ' ', 0, 1);
PIB5 = dlmread('one-cloud/non-proc_proc/RISR5', ' ', 0, 1);


OIB6 = dlmread('one-cloud/officeIoT/RISR6', ' ', 0, 1);
HIB6 = dlmread('one-cloud/homeIoT/RISR6', ' ', 0, 1);
BIB6 = dlmread('one-cloud/buses/RISR6', ' ', 0, 1);
PIB6 = dlmread('one-cloud/non-proc_proc/RISR6', ' ', 0, 1);


%deleted message numbers, to compare to storage usage...? OR deleted
%messages size to compare to used storage
ODT1 = dlmread('one-cloud/officeIoT/RDMR1', ' ', 0, 1);
HDT1 = dlmread('one-cloud/homeIoT/RDMR1', ' ', 0, 1);
BDT1 = dlmread('one-cloud/buses/RDMR1', ' ', 0, 1);
PDT1 = dlmread('one-cloud/non-proc_proc/RDMR1', ' ', 0, 1);


ODT2 = dlmread('one-cloud/officeIoT/RDMR2', ' ', 0, 1);
HDT2 = dlmread('one-cloud/homeIoT/RDMR2', ' ', 0, 1);
BDT2 = dlmread('one-cloud/buses/RDMR2', ' ', 0, 1);
PDT2 = dlmread('one-cloud/non-proc_proc/RDMR2', ' ', 0, 1);


ODT3 = dlmread('one-cloud/officeIoT/RDMR3', ' ', 0, 1);
HDT3 = dlmread('one-cloud/homeIoT/RDMR3', ' ', 0, 1);
BDT3 = dlmread('one-cloud/buses/RDMR3', ' ', 0, 1);
PDT3 = dlmread('one-cloud/non-proc_proc/RDMR3', ' ', 0, 1);


ODT4 = dlmread('one-cloud/officeIoT/RDMR4', ' ', 0, 1);
HDT4 = dlmread('one-cloud/homeIoT/RDMR4', ' ', 0, 1);
BDT4 = dlmread('one-cloud/buses/RDMR4', ' ', 0, 1);
PDT4 = dlmread('one-cloud/non-proc_proc/RDMR4', ' ', 0, 1);


ODT5 = dlmread('one-cloud/officeIoT/RDMR5', ' ', 0, 1);
HDT5 = dlmread('one-cloud/homeIoT/RDMR5', ' ', 0, 1);
BDT5 = dlmread('one-cloud/buses/RDMR5', ' ', 0, 1);
PDT5 = dlmread('one-cloud/non-proc_proc/RDMR5', ' ', 0, 1);


ODT6 = dlmread('one-cloud/officeIoT/RDMR6', ' ', 0, 1);
HDT6 = dlmread('one-cloud/homeIoT/RDMR6', ' ', 0, 1);
BDT6 = dlmread('one-cloud/buses/RDMR6', ' ', 0, 1);
PDT6 = dlmread('one-cloud/non-proc_proc/RDMR6', ' ', 0, 1);



[r3, c3] = size(SS1);

for repo = 1:c3
    ostor_fill(repo, 1) = mean(SS1(:, repo));
    if (isnan(mean(SS1(:, repo))))
        ostor_fill(repo, 1) = 0;
    end
    ostor_fill(repo, 2) = mean(SS2(:, repo));
    if (isnan(mean(SS2(:, repo))))
        ostor_fill(repo, 2) = 0;
    end
    ostor_fill(repo, 3) = mean(SS3(:, repo));
    if (isnan(mean(SS3(:, repo))))
        ostor_fill(repo, 3) = 0;
    end
    ostor_fill(repo, 4) = mean(SS4(:, repo));
    if (isnan(mean(SS4(:, repo))))
        ostor_fill(repo, 4) = 0;
    end
    ostor_fill(repo, 5) = mean(SS5(:, repo));
    if (isnan(mean(SS5(:, repo))))
        ostor_fill(repo, 5) = 0;
    end
    ostor_fill(repo, 6) =  mean(SS6(:, repo));
    if (isnan(mean(SS6(:, repo))))
        ostor_fill(repo, 6) = 0;
    end
    hstor_fill(repo, 1) = mean(HS1(:, repo));
    if (isnan(mean(HS1(:, repo))))
        hstor_fill(repo, 1) = 0;
    end
    hstor_fill(repo, 2) = mean(HS2(:, repo));
    if (isnan(mean(HS2(:, repo))))
        hstor_fill(repo, 2) = 0;
    end
    hstor_fill(repo, 3) = mean(HS3(:, repo));
    if (isnan(mean(HS3(:, repo))))
        hstor_fill(repo, 3) = 0;
    end
    hstor_fill(repo, 4) = mean(HS4(:, repo));
    if (isnan(mean(HS4(:, repo))))
        hstor_fill(repo, 4) = 0;
    end
    hstor_fill(repo, 5) = mean(HS5(:, repo));
    if (isnan(mean(HS5(:, repo))))
        hstor_fill(repo, 5) = 0;
    end
    hstor_fill(repo, 6) = mean(HS6(:, repo));
    if (isnan(mean(HS6(:, repo))))
        hstor_fill(repo, 6) = 0;
    end
    bstor_fill(repo, 1) = mean(BS1(:, repo));
    if (isnan(mean(BS1(:, repo))))
        bstor_fill(repo, 1) = 0;
    end
    bstor_fill(repo, 2) = mean(BS2(:, repo));
    if (isnan(mean(BS2(:, repo))))
        bstor_fill(repo, 2) = 0;
    end
    bstor_fill(repo, 3) = mean(BS3(:, repo));
    if (isnan(mean(BS3(:, repo))))
        bstor_fill(repo, 3) = 0;
    end
    bstor_fill(repo, 4) = mean(BS4(:, repo));
    if (isnan(mean(BS4(:, repo))))
        bstor_fill(repo, 4) = 0;
    end
    bstor_fill(repo, 5) = mean(BS5(:, repo));
    if (isnan(mean(BS5(:, repo))))
        bstor_fill(repo, 5) = 0;
    end
    bstor_fill(repo, 6) =  mean(BS6(:, repo));
    if (isnan(mean(BS6(:, repo))))
        bstor_fill(repo, 6) = 0;
    end
    pstor_fill(repo, 1) = mean(PS1(:, repo));
    if (isnan(mean(PS1(:, repo))))
        pstor_fill(repo, 1) = 0;
    end
    pstor_fill(repo, 2) = mean(PS2(:, repo));
    if (isnan(mean(PS2(:, repo))))
        pstor_fill(repo, 2) = 0;
    end
    pstor_fill(repo, 3) = mean(PS3(:, repo));
    if (isnan(mean(PS3(:, repo))))
        pstor_fill(repo, 3) = 0;
    end
    pstor_fill(repo, 4) = mean(PS4(:, repo));
    if (isnan(mean(PS4(:, repo))))
        pstor_fill(repo, 4) = 0;
    end
    pstor_fill(repo, 5) = mean(PS5(:, repo));
    if (isnan(mean(PS5(:, repo))))
        pstor_fill(repo, 5) = 0;
    end
    pstor_fill(repo, 6) =  mean(PS6(:, repo));
    if (isnan(mean(PS6(:, repo))))
        pstor_fill(repo, 6) = 0;
    end
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([hstor_fill(:,1), hstor_fill(:,2), hstor_fill(:,3), hstor_fill(:,4), hstor_fill(:,5), hstor_fill(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(nonzeros(ostor_fill(:,1))), mean(nonzeros(ostor_fill(:,2))), mean(nonzeros(ostor_fill(:,3))), mean(nonzeros(ostor_fill(:,4))), mean(nonzeros(ostor_fill(:,5))), mean(nonzeros(ostor_fill(:,6)));
    mean(nonzeros(hstor_fill(:,1))), mean(nonzeros(hstor_fill(:,2))), mean(nonzeros(hstor_fill(:,3))), mean(nonzeros(hstor_fill(:,4))), mean(nonzeros(hstor_fill(:,5))), mean(nonzeros(hstor_fill(:,6)));
    mean(nonzeros(bstor_fill(:,1))), mean(nonzeros(bstor_fill(:,2))), mean(nonzeros(bstor_fill(:,3))), mean(nonzeros(bstor_fill(:,4))), mean(nonzeros(bstor_fill(:,5))), mean(nonzeros(bstor_fill(:,6)));
    mean(nonzeros(pstor_fill(:,1))), mean(nonzeros(pstor_fill(:,2))), mean(nonzeros(pstor_fill(:,3))), mean(nonzeros(pstor_fill(:,4))), mean(nonzeros(pstor_fill(:,5))), mean(nonzeros(pstor_fill(:,6)))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
%TSDS:
% Modify this:
lgd1 =legend('1: f; 0.1; 1; 1.1; 100; 3:1; \newline2: f; 0.2; 3; 3; 300; 4:1; \newline3: t; 0.1; 4; 100; 4:1; \newline4: t; 0.1; 1; 2; 100; 1:2', ...
            '1: f; 0.1; 1; 1.3; 100; 3:1; \newline2: f; 0.2; 3; 3.2; 600; 4:1; \newline3: t; 0.1; 2.2; 500; 4:1;  \newline4: t; 0.1; 1; 2; 500; 2:2', ...
            '1: f; 0.1; 1.5; 2; 100; 3:1; \newline2: f; 0.2; 3; 50; 500; 4:1; \newline3: f; 0.1; 3; 100 4:1; \newline4: f; 0.1; 1; 2; 100; 4:2', ...
            '1: f; 0.1; 2; 2.5; 300; 3:1; \newline2: f; 0.1; 2; 3; 600; 4:1; \newline3: f; 0.1; 4; 300 4:1; \newline4: f; 0.1; 1; 2; 300; 5:2', ...
            '1: f; 0.1; 3; 3.2; 600; 3:1; \newline2: f; 0.1; 2; 3.2; 800; 4:1; \newline3: f; 0.1; 2.2; 600 4:1; \newline4: f; 0.1; 1; 2; 600; 3:1', ...
            '1: f; 0.1; 1; 2; 900; 4:1; 50k \newline2: f; 0.1; 2; 50; 1000; 4:1; 1M; \newline3: f; 0.1; 3; 900 4:1; 1M; \newline4: f; 0.1; 1; 2; 900; 4:1; 500k', 'Location', 'southoutside');
title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio; Message Size (last, B)');
%lgd1.NumColumns = 6;
% xlim([17 48]);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])

figure

y = [mean(nonzeros(ostor_fill(:,1))), mean(nonzeros(ostor_fill(:,2))), mean(nonzeros(ostor_fill(:,3))), mean(nonzeros(ostor_fill(:,4))), mean(nonzeros(ostor_fill(:,5))), mean(nonzeros(ostor_fill(:,6)));
    mean(nonzeros(hstor_fill(:,1))), mean(nonzeros(hstor_fill(:,2))), mean(nonzeros(hstor_fill(:,3))), mean(nonzeros(hstor_fill(:,4))), mean(nonzeros(hstor_fill(:,5))), mean(nonzeros(hstor_fill(:,6)));
    mean(nonzeros(bstor_fill(:,1))), mean(nonzeros(bstor_fill(:,2))), mean(nonzeros(bstor_fill(:,3))), mean(nonzeros(bstor_fill(:,4))), mean(nonzeros(bstor_fill(:,5))), mean(nonzeros(bstor_fill(:,6)));
    mean(nonzeros(pstor_fill(:,1))), mean(nonzeros(pstor_fill(:,2))), mean(nonzeros(pstor_fill(:,3))), mean(nonzeros(pstor_fill(:,4))), mean(nonzeros(pstor_fill(:,5))), mean(nonzeros(pstor_fill(:,6)))];
bar(y,'group')
set(gca,'xtick',1:4)
xt=[1:4]-0.25;
yt=y(:,1)+50;
ytxt=num2str(y(:,1),'%.1f');
text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')



for repo = 1:c3-1
    
    osat(repo, 1) = mean(OCB1(:, repo));
    if (isnan(mean(OCB1(:, repo))))
        osat(repo, 1) = 0;
    end
    osat(repo, 2) = mean(OCB2(:, repo));
    if (isnan(mean(OCB2(:, repo))))
        osat(repo, 2) = 0;
    end
    osat(repo, 3) = mean(OCB3(:, repo));
    if (isnan(mean(OCB3(:, repo))))
        osat(repo, 3) = 0;
    end
    osat(repo, 4) = mean(OCB4(:, repo));
    if (isnan(mean(OCB4(:, repo))))
        osat(repo, 4) = 0;
    end
    osat(repo, 5) = mean(OCB5(:, repo));
    if (isnan(mean(OCB5(:, repo))))
        osat(repo, 5) = 0;
    end
    osat(repo, 6) =  mean(OCB6(:, repo));
    if (isnan(mean(OCB6(:, repo))))
        osat(repo, 6) = 0;
    end
    hsat(repo, 1) = mean(HCB1(:, repo));
    if (isnan(mean(HCB1(:, repo))))
        hsat(repo, 1) = 0;
    end
    hsat(repo, 2) = mean(HCB2(:, repo));
    if (isnan(mean(HCB2(:, repo))))
        hsat(repo, 2) = 0;
    end
    hsat(repo, 3) = mean(HCB3(:, repo));
    if (isnan(mean(HCB3(:, repo))))
        hsat(repo, 3) = 0;
    end
    hsat(repo, 4) = mean(HCB4(:, repo));
    if (isnan(mean(HCB4(:, repo))))
        hsat(repo, 4) = 0;
    end
    hsat(repo, 5) = mean(HCB5(:, repo));
    if (isnan(mean(HCB5(:, repo))))
        hsat(repo, 5) = 0;
    end
    hsat(repo, 6) = mean(HCB6(:, repo));
    if (isnan(mean(HCB6(:, repo))))
        hsat(repo, 6) = 0;
    end
    bsat(repo, 1) = mean(BCB1(:, repo));
    if (isnan(mean(BCB1(:, repo))))
        bsat(repo, 1) = 0;
    end
    bsat(repo, 2) = mean(BCB2(:, repo));
    if (isnan(mean(BCB2(:, repo))))
        bsat(repo, 2) = 0;
    end
    bsat(repo, 3) = mean(BCB3(:, repo));
    if (isnan(mean(BCB3(:, repo))))
        bsat(repo, 3) = 0;
    end
    bsat(repo, 4) = mean(BCB4(:, repo));
    if (isnan(mean(BCB4(:, repo))))
        bsat(repo, 4) = 0;
    end
    bsat(repo, 5) = mean(BCB5(:, repo));
    if (isnan(mean(BCB5(:, repo))))
        bsat(repo, 5) = 0;
    end
    bsat(repo, 6) =  mean(BCB6(:, repo));
    if (isnan(mean(BCB6(:, repo))))
        bsat(repo, 6) = 0;
    end
    psat(repo, 1) = mean(PCB1(:, repo));
    if (isnan(mean(PCB1(:, repo))))
        psat(repo, 1) = 0;
    end
    psat(repo, 2) = mean(PCB2(:, repo));
    if (isnan(mean(PCB2(:, repo))))
        psat(repo, 2) = 0;
    end
    psat(repo, 3) = mean(PCB3(:, repo));
    if (isnan(mean(PCB3(:, repo))))
        psat(repo, 3) = 0;
    end
    psat(repo, 4) = mean(PCB4(:, repo));
    if (isnan(mean(PCB4(:, repo))))
        psat(repo, 4) = 0;
    end
    psat(repo, 5) = mean(PCB5(:, repo));
    if (isnan(mean(PCB5(:, repo))))
        psat(repo, 5) = 0;
    end
    psat(repo, 6) =  mean(PCB6(:, repo));
    if (isnan(mean(PCB6(:, repo))))
        psat(repo, 6) = 0;
    end
    ousat(repo, 1) = mean(OSB1(:, repo));
    if (isnan(mean(OSB1(:, repo))))
        ousat(repo, 1) = 0;
    end
    ousat(repo, 2) = mean(OSB2(:, repo));
    if (isnan(mean(OSB2(:, repo))))
        ousat(repo, 2) = 0;
    end
    ousat(repo, 3) = mean(OSB3(:, repo));
    if (isnan(mean(OSB3(:, repo))))
        ousat(repo, 3) = 0;
    end
    ousat(repo, 4) = mean(OSB4(:, repo));
    if (isnan(mean(OSB4(:, repo))))
        ousat(repo, 4) = 0;
    end
    ousat(repo, 5) = mean(OSB5(:, repo));
    if (isnan(mean(OSB5(:, repo))))
        ousat(repo, 5) = 0;
    end
    ousat(repo, 6) =  mean(OSB6(:, repo));
    if (isnan(mean(OSB6(:, repo))))
        ousat(repo, 6) = 0;
    end
    husat(repo, 1) = mean(HSB1(:, repo));
    if (isnan(mean(HSB1(:, repo))))
        husat(repo, 1) = 0;
    end
    husat(repo, 2) = mean(HSB2(:, repo));
    if (isnan(mean(HSB2(:, repo))))
        husat(repo, 2) = 0;
    end
    husat(repo, 3) = mean(HSB3(:, repo));
    if (isnan(mean(HSB3(:, repo))))
        husat(repo, 3) = 0;
    end
    husat(repo, 4) = mean(HSB4(:, repo));
    if (isnan(mean(HSB4(:, repo))))
        husat(repo, 4) = 0;
    end
    husat(repo, 5) = mean(HSB5(:, repo));
    if (isnan(mean(HSB5(:, repo))))
        husat(repo, 5) = 0;
    end
    husat(repo, 6) = mean(HSB6(:, repo));
    if (isnan(mean(HSB6(:, repo))))
        husat(repo, 6) = 0;
    end
    busat(repo, 1) = mean(BSB1(:, repo));
    if (isnan(mean(BSB1(:, repo))))
        busat(repo, 1) = 0;
    end
    busat(repo, 2) = mean(BSB2(:, repo));
    if (isnan(mean(BSB2(:, repo))))
        busat(repo, 2) = 0;
    end
    busat(repo, 3) = mean(BSB3(:, repo));
    if (isnan(mean(BSB3(:, repo))))
        busat(repo, 3) = 0;
    end
    busat(repo, 4) = mean(BSB4(:, repo));
    if (isnan(mean(BSB4(:, repo))))
        busat(repo, 4) = 0;
    end
    busat(repo, 5) = mean(BSB5(:, repo));
    if (isnan(mean(BSB5(:, repo))))
        busat(repo, 5) = 0;
    end
    busat(repo, 6) =  mean(BSB6(:, repo));
    if (isnan(mean(BSB6(:, repo))))
        busat(repo, 6) = 0;
    end
    pusat(repo, 1) = mean(PSB1(:, repo));
    if (isnan(mean(PSB1(:, repo))))
        pusat(repo, 1) = 0;
    end
    pusat(repo, 2) = mean(PSB2(:, repo));
    if (isnan(mean(PSB2(:, repo))))
        pusat(repo, 2) = 0;
    end
    pusat(repo, 3) = mean(PSB3(:, repo));
    if (isnan(mean(PSB3(:, repo))))
        pusat(repo, 3) = 0;
    end
    pusat(repo, 4) = mean(PSB4(:, repo));
    if (isnan(mean(PSB4(:, repo))))
        pusat(repo, 4) = 0;
    end
    pusat(repo, 5) = mean(PSB5(:, repo));
    if (isnan(mean(PSB5(:, repo))))
        pusat(repo, 5) = 0;
    end
    pusat(repo, 6) =  mean(PSB6(:, repo));
    if (isnan(mean(PSB6(:, repo))))
        pusat(repo, 6) = 0;
    end
    
    
    
    oin(repo, 1) = mean(OIB1(:, repo));
    if (isnan(mean(OIB1(:, repo))))
        oin(repo, 1) = 0;
    end
    oin(repo, 2) = mean(OIB2(:, repo));
    if (isnan(mean(OIB2(:, repo))))
        oin(repo, 2) = 0;
    end
    oin(repo, 3) = mean(OIB3(:, repo));
    if (isnan(mean(OIB3(:, repo))))
        oin(repo, 3) = 0;
    end
    oin(repo, 4) = mean(OIB4(:, repo));
    if (isnan(mean(OIB4(:, repo))))
        oin(repo, 4) = 0;
    end
    oin(repo, 5) = mean(OIB5(:, repo));
    if (isnan(mean(OIB5(:, repo))))
        oin(repo, 5) = 0;
    end
    oin(repo, 6) =  mean(OIB6(:, repo));
    if (isnan(mean(OIB6(:, repo))))
        oin(repo, 6) = 0;
    end
    hin(repo, 1) = mean(HIB1(:, repo));
    if (isnan(mean(HIB1(:, repo))))
        hin(repo, 1) = 0;
    end
    hin(repo, 2) = mean(HIB2(:, repo));
    if (isnan(mean(HIB2(:, repo))))
        hin(repo, 2) = 0;
    end
    hin(repo, 3) = mean(HIB3(:, repo));
    if (isnan(mean(HIB3(:, repo))))
        hin(repo, 3) = 0;
    end
    hin(repo, 4) = mean(HIB4(:, repo));
    if (isnan(mean(HIB4(:, repo))))
        hin(repo, 4) = 0;
    end
    hin(repo, 5) = mean(HIB5(:, repo));
    if (isnan(mean(HIB5(:, repo))))
        hin(repo, 5) = 0;
    end
    hin(repo, 6) = mean(HIB6(:, repo));
    if (isnan(mean(HIB6(:, repo))))
        hin(repo, 6) = 0;
    end
    bin(repo, 1) = mean(BIB1(:, repo));
    if (isnan(mean(BIB1(:, repo))))
        bin(repo, 1) = 0;
    end
    bin(repo, 2) = mean(BIB2(:, repo));
    if (isnan(mean(BIB2(:, repo))))
        bin(repo, 2) = 0;
    end
    bin(repo, 3) = mean(BIB3(:, repo));
    if (isnan(mean(BIB3(:, repo))))
        bin(repo, 3) = 0;
    end
    bin(repo, 4) = mean(BIB4(:, repo));
    if (isnan(mean(BIB4(:, repo))))
        bin(repo, 4) = 0;
    end
    bin(repo, 5) = mean(BIB5(:, repo));
    if (isnan(mean(BIB5(:, repo))))
        bin(repo, 5) = 0;
    end
    bin(repo, 6) =  mean(BIB6(:, repo));
    if (isnan(mean(BIB6(:, repo))))
        bin(repo, 6) = 0;
    end
    pin(repo, 1) = mean(PIB1(:, repo));
    if (isnan(mean(PIB1(:, repo))))
        pin(repo, 1) = 0;
    end
    pin(repo, 2) = mean(PIB2(:, repo));
    if (isnan(mean(PIB2(:, repo))))
        pin(repo, 2) = 0;
    end
    pin(repo, 3) = mean(PIB3(:, repo));
    if (isnan(mean(PIB3(:, repo))))
        pin(repo, 3) = 0;
    end
    pin(repo, 4) = mean(PIB4(:, repo));
    if (isnan(mean(PIB4(:, repo))))
        pin(repo, 4) = 0;
    end
    pin(repo, 5) = mean(PIB5(:, repo));
    if (isnan(mean(PIB5(:, repo))))
        pin(repo, 5) = 0;
    end
    pin(repo, 6) =  mean(PIB6(:, repo));
    if (isnan(mean(PIB6(:, repo))))
        pin(repo, 6) = 0;
    end
    
    
    
    odel(repo, 1) = mean(OD1(:, repo))*10800;
    odel(repo, 2) = mean(OD2(:, repo))*10800;
    odel(repo, 3) = mean(OD3(:, repo))*10800;
    odel(repo, 4) = mean(OD4(:, repo))*10800;
    odel(repo, 5) = mean(OD5(:, repo))*10800;
    odel(repo, 6) =  mean(OD6(:, repo))*10800;
    hdel(repo, 1) = mean(HD1(:, repo))*10800;
    hdel(repo, 2) = mean(HD2(:, repo))*10800;
    hdel(repo, 3) = mean(HD3(:, repo))*10800;
    hdel(repo, 4) = mean(HD4(:, repo))*10800;
    hdel(repo, 5) = mean(HD5(:, repo))*10800;
    hdel(repo, 6) = mean(HD6(:, repo))*10800;
    bdel(repo, 1) = mean(BD1(:, repo))*10800;
    bdel(repo, 2) = mean(BD2(:, repo))*10800;
    bdel(repo, 3) = mean(BD3(:, repo))*10800;
    bdel(repo, 4) = mean(BD4(:, repo))*10800;
    bdel(repo, 5) = mean(BD5(:, repo))*10800;
    bdel(repo, 6) =  mean(BD6(:, repo))*10800;
    pdel(repo, 1) = mean(PD1(:, repo))*10800;
    pdel(repo, 2) = mean(PD2(:, repo))*10800;
    pdel(repo, 3) = mean(PD3(:, repo))*10800;
    pdel(repo, 4) = mean(PD4(:, repo))*10800;
    pdel(repo, 5) = mean(PD5(:, repo))*10800;
    pdel(repo, 6) =  mean(PD6(:, repo))*10800;
    
    
    
    osat_up(repo, 1) = mean(osat(repo, 1))/(mean(ousat(repo, 1))+mean(osat(repo, 1)));
    if (isnan(mean(osat(repo, 1))/(mean(ousat(repo, 1))+mean(osat(repo, 1)))))
        osat_up(repo, 1) = 0;
    end
    osat_up(repo, 2) = mean(osat(repo, 2))/(mean(ousat(repo, 2))+mean(osat(repo, 2)));
    if (isnan(mean(osat(repo, 2))/(mean(ousat(repo, 2))+mean(osat(repo, 2)))))
        osat_up(repo, 2) = 0;
    end
    osat_up(repo, 3) = mean(osat(repo, 3))/(mean(ousat(repo, 3))+mean(osat(repo, 3)));
    if (isnan(mean(osat(repo, 3))/(mean(ousat(repo, 3))+mean(osat(repo, 3)))))
        osat_up(repo, 3) = 0;
    end
    osat_up(repo, 4) = mean(osat(repo, 4))/(mean(ousat(repo, 4))+mean(osat(repo, 4)));
    if (isnan(mean(osat(repo, 4))/(mean(ousat(repo, 4))+mean(osat(repo, 4)))))
        osat_up(repo, 4) = 0;
    end
    osat_up(repo, 5) = mean(osat(repo, 5))/(mean(ousat(repo, 5))+mean(osat(repo, 5)));
    if (isnan(mean(osat(repo, 5))/(mean(ousat(repo, 5))+mean(osat(repo, 5)))))
        osat_up(repo, 5) = 0;
    end
    osat_up(repo, 6) =  mean(osat(repo, 6))/(mean(ousat(repo, 6))+mean(osat(repo, 6)));
    if (isnan(mean(osat(repo, 6))/(mean(ousat(repo, 6))+mean(osat(repo, 6)))))
        osat_up(repo, 6) = 0;
    end
    hsat_up(repo, 1) = mean(hsat(repo, 1))/(mean(husat(repo, 1))+mean(hsat(repo, 1)));
    if (isnan(mean(hsat(repo, 1))/(mean(husat(repo, 1))+mean(hsat(repo, 1)))))
        hsat_up(repo, 1) = 0;
    end
    hsat_up(repo, 2) = mean(hsat(repo, 2))/(mean(husat(repo, 2))+mean(hsat(repo, 2)));
    if (isnan(mean(hsat(repo, 2))/(mean(husat(repo, 2))+mean(hsat(repo, 2)))))
        hsat_up(repo, 2) = 0;
    end
    hsat_up(repo, 3) = mean(hsat(repo, 3))/(mean(husat(repo, 3))+mean(hsat(repo, 3)));
    if (isnan(mean(hsat(repo, 3))/(mean(husat(repo, 3))+mean(hsat(repo, 3)))))
        hsat_up(repo, 3) = 0;
    end
    hsat_up(repo, 4) = mean(hsat(repo, 4))/(mean(husat(repo, 4))+mean(hsat(repo, 4)));
    if (isnan(mean(hsat(repo, 4))/(mean(husat(repo, 4))+mean(hsat(repo, 4)))))
        hsat_up(repo, 4) = 0;
    end
    hsat_up(repo, 5) = mean(hsat(repo, 5))/(mean(husat(repo, 5))+mean(hsat(repo, 5)));
    if (isnan(mean(hsat(repo, 6))/(mean(husat(repo, 5))+mean(hsat(repo, 5)))))
        hsat_up(repo, 5) = 0;
    end
    hsat_up(repo, 6) = mean(hsat(repo, 6))/(mean(husat(repo, 6))+mean(hsat(repo, 6)));
    if (isnan(mean(hsat(repo, 6))/(mean(husat(repo, 6))+mean(hsat(repo, 6)))))
        hsat_up(repo, 6) = 0;
    end
    bsat_up(repo, 1) = mean(bsat(repo, 1))/(mean(busat(repo, 1))+mean(bsat(repo, 1)));
    if (isnan(mean(bsat(repo, 1))/(mean(busat(repo, 1))+mean(bsat(repo, 1)))))
        bsat_up(repo, 1) = 0;
    end
    bsat_up(repo, 2) = mean(bsat(repo, 2))/(mean(busat(repo, 2))+mean(bsat(repo, 2)));
    if (isnan(mean(bsat(repo, 2))/(mean(busat(repo, 2))+mean(bsat(repo, 2)))))
        bsat_up(repo, 2) = 0;
    end
    bsat_up(repo, 3) = mean(bsat(repo, 3))/(mean(busat(repo, 3))+mean(bsat(repo, 3)));
    if (isnan(mean(bsat(repo, 3))/(mean(busat(repo, 3))+mean(bsat(repo, 3)))))
        bsat_up(repo, 3) = 0;
    end
    bsat_up(repo, 4) = mean(bsat(repo, 4))/(mean(busat(repo, 4))+mean(bsat(repo, 4)));
    if (isnan(mean(bsat(repo, 4))/(mean(busat(repo, 4))+mean(bsat(repo, 4)))))
        bsat_up(repo, 4) = 0;
    end
    bsat_up(repo, 5) = mean(bsat(repo, 5))/(mean(busat(repo, 5))+mean(bsat(repo, 5)));
    if (isnan(mean(bsat(repo, 5))/(mean(busat(repo, 5))+mean(bsat(repo, 5)))))
        bsat_up(repo, 5) = 0;
    end
    bsat_up(repo, 6) =  mean(bsat(repo, 6))/(mean(busat(repo, 6))+mean(bsat(repo, 6)));
    if (isnan(mean(bsat(repo, 6))/(mean(busat(repo, 6))+mean(bsat(repo, 6)))))
        bsat_up(repo, 6) = 0;
    end
    psat_up(repo, 1) = mean(psat(repo, 1))/(mean(pusat(repo, 1))+mean(psat(repo, 1)));
    if (isnan(mean(psat(repo, 1))/(mean(pusat(repo, 1))+mean(psat(repo, 1)))))
        psat_up(repo, 1) = 0;
    end
    psat_up(repo, 2) = mean(psat(repo, 2))/(mean(pusat(repo, 2))+mean(psat(repo, 2)));
    if (isnan(mean(psat(repo, 2))/(mean(pusat(repo, 2))+mean(psat(repo, 2)))))
        psat_up(repo, 2) = 0;
    end
    psat_up(repo, 3) = mean(psat(repo, 3))/(mean(pusat(repo, 3))+mean(psat(repo, 3)));
    if (isnan(mean(psat(repo, 3))/(mean(pusat(repo, 3))+mean(psat(repo, 3)))))
        psat_up(repo, 3) = 0;
    end
    psat_up(repo, 4) = mean(psat(repo, 4))/(mean(pusat(repo, 4))+mean(psat(repo, 4)));
    if (isnan(mean(psat(repo, 4))/(mean(pusat(repo, 4))+mean(psat(repo, 4)))))
        psat_up(repo, 4) = 0;
    end
    psat_up(repo, 5) = mean(psat(repo, 5))/(mean(pusat(repo, 5))+mean(psat(repo, 5)));
    if (isnan(mean(psat(repo, 5))/(mean(pusat(repo, 5))+mean(psat(repo, 5)))))
        psat_up(repo, 5) = 0;
    end
    psat_up(repo, 6) =  mean(psat(repo, 6))/(mean(pusat(repo, 6))+mean(psat(repo, 6)));
    if (isnan(mean(psat(repo, 6))/(mean(pusat(repo, 6))+mean(psat(repo, 6)))))
        psat_up(repo, 6) = 0;
    end
    
    
    
    odel_up(repo, 1) = (mean(OD1(repo, :)))/(mean(OD1(repo, :))+oin(repo, 1));
    odel_up(repo, 2) = (mean(OD2(repo, :)))/(mean(OD2(repo, :))+oin(repo, 2));
    odel_up(repo, 3) = (mean(OD3(repo, :)))/(mean(OD3(repo, :))+oin(repo, 3));
    odel_up(repo, 4) = (mean(OD4(repo, :)))/(mean(OD4(repo, :))+oin(repo, 4));
    odel_up(repo, 5) = (mean(OD5(repo, :)))/(mean(OD5(repo, :))+oin(repo, 5));
    odel_up(repo, 6) = (mean(OD6(repo, :)))/(mean(OD6(repo, :))+oin(repo, 6));
    hdel_up(repo, 1) = (mean(OD1(repo, :)))/(mean(HD1(repo, :))+oin(repo, 1));
    hdel_up(repo, 2) = (mean(HD2(repo, :)))/(mean(HD2(repo, :))+oin(repo, 2));
    hdel_up(repo, 3) = (mean(HD3(repo, :)))/(mean(HD3(repo, :))+oin(repo, 3));
    hdel_up(repo, 4) = (mean(HD4(repo, :)))/(mean(HD4(repo, :))+oin(repo, 4));
    hdel_up(repo, 5) = (mean(HD5(repo, :)))/(mean(HD5(repo, :))+oin(repo, 5));
    hdel_up(repo, 6) = (mean(HD6(repo, :)))/(mean(HD6(repo, :))+oin(repo, 6));
    bdel_up(repo, 1) = (mean(BD1(repo, :)))/(mean(BD1(repo, :))+oin(repo, 1));
    bdel_up(repo, 2) = (mean(BD2(repo, :)))/(mean(BD2(repo, :))+oin(repo, 2));
    bdel_up(repo, 3) = (mean(BD3(repo, :)))/(mean(BD3(repo, :))+oin(repo, 3));
    bdel_up(repo, 4) = (mean(BD4(repo, :)))/(mean(BD4(repo, :))+oin(repo, 4));
    bdel_up(repo, 5) = (mean(BD5(repo, :)))/(mean(BD5(repo, :))+oin(repo, 5));
    bdel_up(repo, 6) = (mean(BD6(repo, :)))/(mean(OD6(repo, :))+oin(repo, 6));
    pdel_up(repo, 1) = (mean(PD1(repo, :)))/(mean(PD1(repo, :))+oin(repo, 1));
    pdel_up(repo, 2) = (mean(PD2(repo, :)))/(mean(PD2(repo, :))+oin(repo, 2));
    pdel_up(repo, 3) = (mean(PD3(repo, :)))/(mean(PD3(repo, :))+oin(repo, 3));
    pdel_up(repo, 4) = (mean(PD4(repo, :)))/(mean(PD4(repo, :))+oin(repo, 4));
    pdel_up(repo, 5) = (mean(PD5(repo, :)))/(mean(PD5(repo, :))+oin(repo, 5));
    pdel_up(repo, 6) = (mean(PD6(repo, :)))/(mean(PD6(repo, :))+oin(repo, 6));
   
end


figure

% subplot(2,1,1);
% yyaxis left
bar_handle = bar([osat_up(:,1), osat_up(:,2), osat_up(:,3), osat_up(:,4), osat_up(:,5), osat_up(:,6)]);
% title('Processing threads','fontsize',16)
xlabel('Repository number','fontsize',12) 
ylabel('Storage used (B)','fontsize',12)
% xlim([17 48]);
set(bar_handle(1),'FaceColor',[0,0.5,1])
set(bar_handle(2),'FaceColor',[0,1,0])




figure

% subplot(2,1,1);
% yyaxis left
bar([mean(osat_up(:,1)), mean(osat_up(:,2)), mean(osat_up(:,3)), mean(osat_up(:,4)), mean(osat_up(:,5)), mean(osat_up(:,6));
    mean(hsat_up(:,1)), mean(hsat_up(:,2)), mean(hsat_up(:,3)), mean(hsat_up(:,4)), mean(hsat_up(:,5)), mean(hsat_up(:,6));
    mean(bsat_up(:,1)), mean(bsat_up(:,2)), mean(bsat_up(:,3)), mean(bsat_up(:,4)), mean(bsat_up(:,5)), mean(bsat_up(:,6));
    mean(psat_up(:,1)), mean(psat_up(:,2)), mean(psat_up(:,3)), mean(psat_up(:,4)), mean(psat_up(:,5)), mean(psat_up(:,6))]);
% title('Processing threads','fontsize',16)
xlabel('Scenario Number','fontsize',12) 
ylabel('Non-processing Message Satisfied Upload (100%)','fontsize',12)
%TSDS:
% Modify this:
% lgd1 =legend('1: f; 1.1; 100; 3:1 \newline2: f; 3; 100; 4:1; \newline3: t; 4; 100; 4:1; \newline4: t; 2; 100; 1:2', ...
%             '1: f; 1.3; 100; 3:1 \newline2: f; 3.2; 100; 4:1; \newline3: t; 2.2; 500; 4:1;  \newline4: t; 2; 500; 2:2', ...
%             '1: f; 2; 100; 3:1 \newline2: f; 50; 100; 4:1; \newline3: f; 3; 100 4:1; \newline4: f; 2; 100; 4:2', ...
%             '1: f; 1.6; 300; 3:1 \newline2: f; 3; 200; 4:1; \newline3: f; 4; 300 4:1; \newline4: f; 2; 300; 5:2', ...
%             '1: f; 2.2; 600; 3:1 \newline2: f; 3.2; 300; 4:1; \newline3: f; 2.2; 600 4:1; \newline4: f; 2; 600; 3:1', ...
%             '1: f; 2; 900; 4:1 \newline2: f; 50; 600; 4:1; \newline3: f; 3; 900 4:1; \newline4: f; 2; 900; 4:1', 'Location', 'southoutside');
% title(lgd1, 'Scenario Number: Storage Mode; Processing Delay; proc Freshness Period; proc Shelf-Life; non-proc Shelf-Life; Compress:Delete Ratio; Non-Proc:Proc Ratio');
% lgd1.NumColumns = 6;

% figure
% 
% % subplot(2,1,1);
% % yyaxis left
% bar([mean(osat(:,1)), mean(ousat(:,1)), mean(osat(:,2)), mean(ousat(:,2)), mean(osat(:,3)), mean(ousat(:,3)), mean(osat(:,4)), mean(ousat(:,4)), mean(osat(:,5)), mean(ousat(:,5)), mean(osat(:,6)), mean(ousat(:,6));
%     mean(hsat(:,1)), mean(husat(:,1)), mean(hsat(:,2)), mean(husat(:,2)), mean(hsat(:,3)), mean(husat(:,3)), mean(hsat(:,4)), mean(husat(:,4)), mean(hsat(:,5)), mean(husat(:,5)), mean(hsat(:,6)), mean(husat(:,6));
%     mean(bsat(:,1)), mean(busat(:,1)), mean(bsat(:,2)), mean(busat(:,2)), mean(bsat(:,3)), mean(busat(:,3)), mean(bsat(:,4)), mean(busat(:,4)), mean(bsat(:,5)), mean(busat(:,5)), mean(bsat(:,6)), mean(busat(:,6));
%     mean(psat(:,1)), mean(pusat(:,1)), mean(psat(:,2)), mean(pusat(:,2)), mean(psat(:,3)), mean(pusat(:,3)), mean(psat(:,4)), mean(pusat(:,4)), mean(psat(:,5)), mean(pusat(:,5)), mean(psat(:,6)), mean(pusat(:,6))]);
% % title('Processing threads','fontsize',16)
% xlabel('Scenario Number','fontsize',12) 
% ylabel('Non-processing Message Satisfied and UnSatisfied Upload BW (B/s)','fontsize',12)


figure

yyaxis left
y = [mean(nonzeros(hstor_fill(:,1))), mean(nonzeros(hstor_fill(:,2))), mean(nonzeros(hstor_fill(:,3))), mean(nonzeros(hstor_fill(:,4))), mean(nonzeros(hstor_fill(:,5))), mean(nonzeros(hstor_fill(:,6)));
    mean(hsat_up(:,1)), mean(hsat_up(:,2)), mean(hsat_up(:,3)), mean(hsat_up(:,4)), mean(hsat_up(:,5)), mean(hsat_up(:,6));
    mean(nonzeros(pstor_fill(:,1))), mean(nonzeros(pstor_fill(:,2))), mean(nonzeros(pstor_fill(:,3))), mean(nonzeros(pstor_fill(:,4))), mean(nonzeros(pstor_fill(:,5))), mean(nonzeros(pstor_fill(:,6)));
    mean(psat_up(:,1)), mean(psat_up(:,2)), mean(psat_up(:,3)), mean(psat_up(:,4)), mean(psat_up(:,5)), mean(psat_up(:,6));
    mean(nonzeros(hdel(:,1))), mean(nonzeros(hdel(:,2))), mean(nonzeros(hdel(:,3))), mean(nonzeros(hdel(:,4))), mean(nonzeros(hdel(:,5))), mean(nonzeros(hdel(:,6)));
    mean(hdel_up(:,1)), mean(hdel_up(:,2)), mean(hdel_up(:,3)), mean(hdel_up(:,4)), mean(hdel_up(:,5)), mean(hdel_up(:,6));
    mean(nonzeros(pdel(:,1))), mean(nonzeros(pdel(:,2))), mean(nonzeros(pdel(:,3))), mean(nonzeros(pdel(:,4))), mean(nonzeros(pdel(:,5))), mean(nonzeros(pdel(:,6)));
    mean(pdel_up(:,1)), mean(pdel_up(:,2)), mean(pdel_up(:,3)), mean(pdel_up(:,4)), mean(pdel_up(:,5)), mean(pdel_up(:,6))];
bar(y,'group')
set(gca,'xtick',1:8,'XTickLabel',{'Usage', 'Satisfaction', 'Usage', 'Satisfaction', 'Drop Total', 'Drop BW', 'Drop Total', 'Drop BW'})
% xt= [1-0.34, 1-0.2, 1-0.07, 1.07, 1.2, 1.34, 3-0.34, 3-0.2, 3-0.07, 3.07, 3.2, 3.34, 5-0.34, 5-0.2, 5-0.07, 5.07, 5.2, 5.34, 7-0.34, 7-0.2, 7-0.07, 7.07, 7.2, 7.34];
% yt=[y(1,:), y(3,:), y(5,:), y(7,:)]+50;
%xt=[1, 3, 5, 7]-0.25;
%yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% ytxt=num2str([y(1, :)'; y(3, :)'; y(5, :)'; y(7, :)'],'%.1f');
% text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% title('Processing threads','fontsize',16)
% xlabel('Scenario Number','fontsize',12) 
ylabel('Storage usage (B)','fontsize',12)

groupX = [2 4 6 8]; %// central value of each group
groupY = -5; %// vertical position of texts. Adjust as needed
deltaY = .03; %// controls vertical compression of axis. Adjust as needed
groupNames = {'Homes Repos', 'Cars Repos', 'Homes IP', 'Cars IP'}; %// note different lengths to test centering
for g = 1:numel(groupX)
    h = text(groupX(g), groupY, groupNames(g), 'Fontsize', 13, 'Fontweight', 'bold');
        %// create text for group with appropriate font size and weight
    pos = get(h, 'Position');
    ext = get(h, 'Extent');
    pos(1) = pos(1) - ext(3)/2; %// horizontally correct position to make it centered
    set(h, 'Position', pos); %// set corrected position for text
end
pos = get(gca, 'position');
pos(2) = pos(2) + deltaY; %// vertically compress axis to make room for texts
set(gca, 'Position', pos); %/ set corrected position for axis


yyaxis right
y = [mean(nonzeros(hstor_fill(:,1))), mean(nonzeros(hstor_fill(:,2))), mean(nonzeros(hstor_fill(:,3))), mean(nonzeros(hstor_fill(:,4))), mean(nonzeros(hstor_fill(:,5))), mean(nonzeros(hstor_fill(:,6)));
    mean(hsat_up(:,1)), mean(hsat_up(:,2)), mean(hsat_up(:,3)), mean(hsat_up(:,4)), mean(hsat_up(:,5)), mean(hsat_up(:,6));
    mean(nonzeros(pstor_fill(:,1))), mean(nonzeros(pstor_fill(:,2))), mean(nonzeros(pstor_fill(:,3))), mean(nonzeros(pstor_fill(:,4))), mean(nonzeros(pstor_fill(:,5))), mean(nonzeros(pstor_fill(:,6)));
    mean(psat_up(:,1)), mean(psat_up(:,2)), mean(psat_up(:,3)), mean(psat_up(:,4)), mean(psat_up(:,5)), mean(psat_up(:,6));
    mean(nonzeros(hdel(:,1))), mean(nonzeros(hdel(:,2))), mean(nonzeros(hdel(:,3))), mean(nonzeros(hdel(:,4))), mean(nonzeros(hdel(:,5))), mean(nonzeros(hdel(:,6)));
    mean(hdel_up(:,1)), mean(hdel_up(:,2)), mean(hdel_up(:,3)), mean(hdel_up(:,4)), mean(hdel_up(:,5)), mean(hdel_up(:,6));
    mean(nonzeros(pdel(:,1))), mean(nonzeros(pdel(:,2))), mean(nonzeros(pdel(:,3))), mean(nonzeros(pdel(:,4))), mean(nonzeros(pdel(:,5))), mean(nonzeros(pdel(:,6)));
    mean(pdel_up(:,1)), mean(pdel_up(:,2)), mean(pdel_up(:,3)), mean(pdel_up(:,4)), mean(pdel_up(:,5)), mean(pdel_up(:,6))];
bar(1:8,[0,0,0,0,0,0; y(2, :); 0,0,0,0,0,0; y(4, :); 0,0,0,0,0,0; y(6, :); 0,0,0,0,0,0; y(8, :)],'group')
% set(gca,'xtick',1:8)
% xt= [2-0.34, 2-0.2, 2-0.07, 2.07, 2.2, 2.34, 4-0.34, 4-0.2, 4-0.07, 4.07, 4.2, 4.34, 6-0.34, 6-0.2, 6-0.07, 6.07, 6.2, 6.34, 8-0.34, 8-0.2, 8-0.07, 8.07, 8.2, 8.34];
% yt=[y(2,:), y(4,:), y(6,:), y(8,:)]-50;
%xt=[1, 3, 5, 7]-0.25;
%yt=[y(1,1); y(3,1); y(5,1); y(7,1)]+50;
% ytxt=num2str([y(2, :)'; y(4, :)'; y(6, :)'; y(8, :)'],'%.1f');
% text(xt,yt,ytxt,'rotation',90,'fontsize',8,'fontweight','bold')
% title('Processing threads','fontsize',16)
ylabel('Non-processing Message Satisfied Upload (100%)','fontsize',12)
ylim([0 1])





% procApp2.delay = 0.1
% procApp2.freshPer = [1; 1; 1; 1.5; 2; 3; 1]
% procApp2.procShelf = [1.1; 1.3; 2; 1.6; 2.2; 3.3; 2]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 50k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 1,4
% procApp2.passiveRate = [3,1; 3,1; 3,1; 3,1; 3,1; 4,1]


% procApp2.delay = [0.2; 0.2; 0.2; 0.1; 0.1; 0.1]
% procApp2.freshPer = [3; 3; 3; 2; 2; 2]
% procApp2.procShelf = [3; 3.2; 50]
% procApp2.nonprocShelf = [100; 100; 100; 200; 300; 600]
% procApp2.destinationName = r
% procApp2.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = [2,1; 2,1; 2,2; 2,2; 1,2; 1,2]
% procApp2.passiveRate = 4,1


% procApp.storageMode = [true; true; false; false; false; false]
% procApp.maxStorTime = [3000; 3000]
% 
% 
% procApp3.interval = 0.7
% procApp3.delay = [0.3; 0.3; 0.3; 0.3; 0.1; 0.1]
% procApp3.freshPer = [3; 2; 2]
% procApp3.procShelf = [4; 2.2; 3]
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp3.destinationName = r
% procApp3.procSize = 1M
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp3.compressRate = 3,1
% procApp3.passiveRate = [4,1]
% procApp3.passive = false
% 
% 
% procApp2.delay = 0.1
% procApp2.freshPer = 1
% procApp2.procShelf = 2
% procApp3.nonprocShelf = [100; 500; 100; 300; 600; 900]
% procApp2.destinationName = r
% procApp2.procSize = 500k
% # Stored message ratio to processing message (no. of messages to be stored vs. no. of messages to be processed)
% procApp2.compressRate = 2,2
% procApp2.passiveRate = [4,1; 3,1; 5,2; 4,2; 2,2; 1,2]
% procApp2.passive = false


