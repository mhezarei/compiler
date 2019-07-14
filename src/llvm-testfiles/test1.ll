; ModuleID = 'test1.ll'
source_filename = "test1.cpp"
target datalayout = "e-m:e-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

; Function Attrs: noinline norecurse nounwind optnone uwtable
define i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca float, align 4
  %4 = alloca float, align 4
  store i32 0, i32* %1, align 4
  %5 = load i32, i32* %2, align 4
  %6 = add nsw i32 2, %5
  %7 = sitofp i32 %6 to float
  store float %7, float* %4, align 4
  %8 = load float, float* %3, align 4
  %9 = fmul float 1.000000e+00, %8
  %10 = load float, float* %4, align 4
  %11 = fadd float %9, %10
  %12 = fptosi float %11 to i32
  store i32 %12, i32* %2, align 4
  %13 = load i32, i32* %2, align 4
  %14 = sitofp i32 %13 to float
  %15 = load float, float* %3, align 4
  %16 = fcmp oeq float %14, %15
  br i1 %16, label %17, label %18

; <label>:17:                                     ; preds = %0
  store float 3.000000e+00, float* %3, align 4
  br label %19

; <label>:18:                                     ; preds = %0
  store float 4.000000e+00, float* %3, align 4
  br label %19

; <label>:19:                                     ; preds = %18, %17
  ret i32 4
}

attributes #0 = { noinline norecurse nounwind optnone uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+fxsr,+mmx,+sse,+sse2,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"clang version 6.0.0-1ubuntu2 (tags/RELEASE_600/final)"}
